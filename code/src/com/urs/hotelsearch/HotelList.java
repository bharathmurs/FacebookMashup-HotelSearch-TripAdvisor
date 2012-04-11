package com.urs.hotelsearch;

import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.android.FacebookError;
import com.facebook.android.Util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ParseException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class HotelList extends Activity implements Runnable {

	// Stores array of hotels, which will returned from the webservice.
	private Hotel[] hotelList;

	// Handler for listview which displays list of hotels.
	private ListView lvHotels;

	ProgressDialog pd;
	private String strCityName = "", strHotelChain = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.hotel_listview);

		// Shows progress dialog, while the background process happens.
		pd = ProgressDialog.show(this, "Please wait..", "Getting Hotel List",
				true, true);

		// Get the data from the previous screen. (city name and hotel chain)
		Intent sender = getIntent();
		strCityName = sender.getStringExtra("cityName");
		strHotelChain = sender.getStringExtra("hotelChain");

		if (strCityName == null)
			strCityName = "";
		if (strHotelChain == null)
			strHotelChain = "";

		// Create a new thread for background processing.
		// Using UI thread for this is VERY BAD.!
		Thread thread = new Thread(HotelList.this);
		thread.start();
	}

	public void run() {
		// Gets the List of hotels.
		hotelList = GetHotels();
		handler.sendEmptyMessage(0);
	}

	private Hotel[] GetHotels() {

		HttpGet httpget = new HttpGet();
		HttpClient httpclient = new DefaultHttpClient();
		String responseMessage = "";

		try {
			
			// Call the web service with city name and hotel chain.
			// The webservice returns list of hotels in JSON format.
			String SERVER_URL = "http://cs-server.usc.edu:38057/examples/servlet/HotelSearch?cityName="
					+ strCityName + "&hotelChain=" + strHotelChain;

			httpget = new HttpGet(SERVER_URL);
			HttpResponse response = httpclient.execute(httpget);
			responseMessage = EntityUtils.toString(response.getEntity());

			// Parse JSON 
			JSONObject json = Util.parseJson(responseMessage);
			JSONArray hotel = json.getJSONObject("hotels")
					.getJSONArray("hotel");

			Hotel[] hotelList = new Hotel[hotel.length()];
			for (int i = 0; i < hotel.length(); i++) {
				hotelList[i] = new Hotel();
				JSONObject obj = (JSONObject) hotel.get(i);
				hotelList[i].IMAGE = obj.getString("image").substring(0,
						obj.getString("image").indexOf("`"));
				hotelList[i].NAME = obj.getString("name");
				hotelList[i].LOCATION = obj.getString("location");
				hotelList[i].RATING = obj.getString("rating");
				hotelList[i].REVIEWS = obj.getString("no_of_reviews");
				hotelList[i].REVIEW_URL = obj.getString("review_url");
			}
			// If the JSON returned with at least 1 hotel, then return the
			// hotelList,
			// else return null (No hotels returned from JSON).

			if (hotel.length() != 0) {
				return hotelList;
			}

		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (FacebookError e) {
			e.printStackTrace();
		}

		return null;
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (hotelList != null) {
				// Create and initialize custom list
				CustomListView[] data = new CustomListView[hotelList.length];
				for (int i = 0; i < hotelList.length; i++) {
					data[i] = new CustomListView(hotelList[i].IMAGE,
							hotelList[i].NAME.trim() + "\n"
									+ hotelList[i].LOCATION.trim());
				}

				CustomListAdapter adapter = new CustomListAdapter(
						HotelList.this, R.layout.custom_listview, data);

				// Stop the progress dialog.
				pd.dismiss();
				lvHotels = (ListView) findViewById(R.id.lvHotels);
				lvHotels.setAdapter(adapter);

				lvHotels.setOnItemClickListener(new OnItemClickListener() {

					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// On click of a particular hotel,
						// goto a new screen which displays hotel details.
						Intent i = new Intent(getBaseContext(),
								HotelDetail.class);
						i.putExtra("hotel", hotelList[position]);
						startActivity(i);
					}
				});
			} else {
				pd.dismiss();
				Toast.makeText(getBaseContext(),
						"No Hotels Found, try something else.",
						Toast.LENGTH_LONG).show();
			}
		}
	};
}
