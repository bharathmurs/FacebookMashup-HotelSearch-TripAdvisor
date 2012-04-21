package com.urs.hotelsearch;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.R;
import com.facebook.android.Util;
import com.urs.hotelsearch.SessionEvents.AuthListener;
import com.urs.hotelsearch.SessionEvents.LogoutListener;

public class MainScreen extends Activity {
	/** Called when the activity is first created. */

	// Facebook Login/Logout Button
	private LoginButton mLoginButton;
	
	// String that hold the value of hotel chain
	private String strHotelChain = "";
	
	// Handler for Search hotel button
	private Button btnSearchHotel;
	
	// Handler for welcome message Textview
	private TextView lblWelcome;
	
	// Handler for form layout (Contains EditText, Spinner and Search Hotel Button) 
	private LinearLayout layoutForm;
	
	// Handler for city name textbox
	private EditText txtCityName;

	// Items for hotel chain spinner. User can select any of these hotel 
	// chain.
	String[] items_hotel_chain = { "Mariott", "Hilton", "Hyatt", "Accor",
			"InterContinental" };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		if (Utility.APP_ID == null) {
			Util.showAlert(this, "Warning", "Facebook Applicaton ID must be "
					+ "specified before running this example: see Example.java");
		}

		setContentView(R.layout.main);
		
		/* Get Handles for all the controls on the screen which needs
		 * manipulation
		 */
		mLoginButton = (LoginButton) findViewById(R.id.login);
		btnSearchHotel = (Button) findViewById(R.id.btnSearch);
		lblWelcome = (TextView) findViewById(R.id.lblWelcome);
		txtCityName = (EditText) findViewById(R.id.txtCityName);
		layoutForm = (LinearLayout) findViewById(R.id.layoutForm);
		layoutForm.setVisibility(View.INVISIBLE);

	  
		// Create an instance of Facebook object, passing the APP ID.
		Utility.mFacebook = new Facebook(Utility.APP_ID);
		
		Utility.mAsyncRunner = new AsyncFacebookRunner(Utility.mFacebook);

		SessionStore.restore(Utility.mFacebook, this);
		SessionEvents.addAuthListener(new SampleAuthListener());
		SessionEvents.addLogoutListener(new SampleLogoutListener());
		mLoginButton.init(this, Utility.mFacebook);

		Spinner spin = (Spinner) findViewById(R.id.ddlHotelChain);
		spin.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long id) {
				// Set the Hotel Chain selected.
				strHotelChain = items_hotel_chain[position];
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				// So, nothing comes here. ;)
			}
		});

		@SuppressWarnings({ "rawtypes", "unchecked" })
		ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, items_hotel_chain);

		aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spin.setAdapter(aa);

		btnSearchHotel.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// If city name is entered, call new intent by passing,
				// city name and hotel chain to the new intent.
				if (!txtCityName.getText().toString().trim().equals("")) {
					Intent i = new Intent(getBaseContext(), HotelList.class);
					i.putExtra("cityName", txtCityName.getText().toString().replace(" ", "+").trim());
					i.putExtra("hotelChain", strHotelChain);
					startActivity(i);
				}
				// City name cannot be empty, so tell the user.
				else{
					Toast.makeText(getBaseContext(), "Please enter city name", Toast.LENGTH_SHORT).show();
				}
			}
		});

		if (Utility.mFacebook.isSessionValid()) {
			lblWelcome.setText("Welcome, " + Utility.strUsername);
			layoutForm.setVisibility(View.VISIBLE);
		}

	}

	@Override
	protected void onResume() {
		// If user is already logged in, display welcome message.
		// So that he doesn't feel left alone.! ;)
		super.onResume();
		if (Utility.mFacebook.isSessionValid()) {
			Utility.mAsyncRunner.request("me", new SampleRequestListener());
			layoutForm.setVisibility(View.VISIBLE);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Utility.mFacebook.authorizeCallback(requestCode, resultCode, data);
	}

	public class SampleAuthListener implements AuthListener {

		public void onAuthSucceed() {
			Utility.mAsyncRunner.request("me", new SampleRequestListener());
		}

		public void onAuthFail(String error) {
		}
	}

	public class SampleLogoutListener implements LogoutListener {
		public void onLogoutBegin() {
		}

		public void onLogoutFinish() {
			lblWelcome.setText("");
			layoutForm.setVisibility(View.INVISIBLE);
		}
	}

	public class SampleRequestListener extends BaseRequestListener {

		public void onComplete(final String response, final Object state) {
			try {
				// process the response here: executed in background thread
				Log.d("Facebook-Example", "Response: " + response.toString());
				JSONObject json = Util.parseJson(response);
				final String name = json.getString("name");

				// then post the processed result back to the UI thread
				// if we do not do this, an runtime exception will be generated
				// e.g. "CalledFromWrongThreadException: Only the original
				// thread that created a view hierarchy can touch its views."
				MainScreen.this.runOnUiThread(new Runnable() {
					public void run() {
						lblWelcome.setText("Welcome, " + name);
						Utility.strUsername = name;
						layoutForm.setVisibility(View.VISIBLE);
					}
				});
			} catch (JSONException e) {
				Log.w("Facebook-Example", "JSON Error in response");
			} catch (FacebookError e) {
				Log.w("Facebook-Example", "Facebook Error: " + e.getMessage());
			}
		}
	}

}