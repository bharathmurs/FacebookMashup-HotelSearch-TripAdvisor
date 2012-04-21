package com.urs.hotelsearch;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

public class HotelDetail extends Activity {

	private Hotel hotel;

	// Handle for hotel Name label.
	TextView lblHotelName;

	// Handle for hotel location label.
	TextView lblLocation;

	// Handle for ImageView which displays hotel image.
	ImageView imgHotel;

	// Handle for rating bar, which displays the hotel rating.
	RatingBar ratingBar;

	// Handle for Post link to facebook button.
	Button btnPost;
	Context context = this;

	// String which holds the message that needs to be posted to user's
	// facebook wall.
	String strShareMessage;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.hotel_detail);

		// Get handlers for the controls.
		lblHotelName = (TextView) findViewById(R.id.lblHotelName);
		lblLocation = (TextView) findViewById(R.id.lblLocation);
		imgHotel = (ImageView) findViewById(R.id.imgHotel);
		ratingBar = (RatingBar) findViewById(R.id.ratingBar);
		btnPost = (Button) findViewById(R.id.btnPost);

		ratingBar.setEnabled(false);

		Intent sender = getIntent();
		hotel = (Hotel) sender.getSerializableExtra("hotel");

		if (hotel != null) {
			lblHotelName.setText(hotel.NAME);
			lblLocation.setText(hotel.LOCATION);
			imgHotel.setImageBitmap(Utility.getImageBitmap(hotel.IMAGE));
			ratingBar.setNumStars(5);
			ratingBar.setRating(Float.parseFloat(hotel.RATING));
		}

		btnPost.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// On click of post button, create a new dialog
				// which asks the user message to be shared along with the
				// review link.
				Bundle params = new Bundle();

				try {
					JSONObject attachment = new JSONObject();
					attachment.put("message", "");
					attachment.put("name", hotel.NAME);
					attachment.put("href", hotel.REVIEW_URL);
					attachment.put("description", "The hotel is located at "
							+ hotel.LOCATION + " and has a rating of "
							+ hotel.RATING + " out of 5.");

					JSONObject media = new JSONObject();
					media.put("type", "image");
					media.put("src", hotel.IMAGE);
					media.put("href", hotel.IMAGE);
					attachment.put("media", new JSONArray().put(media));

					JSONObject properties = new JSONObject();

					JSONObject prop = new JSONObject();
					prop.put("text", " Click here");
					prop.put("href", hotel.REVIEW_URL);
					properties.put("For reviews and more details ", prop);

					attachment.put("properties", properties);

					params.putString("attachment", attachment.toString());

				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				Utility.mFacebook.dialog(context, "stream.publish", params,
						new DialogListener() {

							public void onComplete(Bundle values) {
								return;
							}

							public void onFacebookError(FacebookError e) {

							}

							public void onError(DialogError e) {

							}

							public void onCancel() {
								return;
							}
						});
			}
		});
	}
}
