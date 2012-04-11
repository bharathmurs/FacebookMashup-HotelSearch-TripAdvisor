package com.urs.hotelsearch;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.FacebookError;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

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
				final Dialog dialog = new Dialog(context);
				dialog.setContentView(R.layout.share_confirm);
				dialog.setTitle("Share on Facebook?");
				TextView lblShareHeading = (TextView) dialog
						.findViewById(R.id.lblShareHeading);
				lblShareHeading.setText("You are about to share, " + hotel.NAME
						+ " on facebook");

				final EditText txtShareMessage = (EditText) dialog
						.findViewById(R.id.txtShareMessage);

				Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
				btnCancel.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				});

				Button btnShare = (Button) dialog.findViewById(R.id.btnShare);
				btnShare.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						strShareMessage = txtShareMessage.getText().toString();
						postToFBWall();
						dialog.dismiss();
					}
				});

				dialog.show();
			}
		});
	}

	private void postToFBWall() {
		if (Utility.mFacebook.isSessionValid()) {
			shareOnFB();
		} else {
		}
	}

	public void shareOnFB() {
		Bundle params = new Bundle();
		params.putString("message", strShareMessage);
		params.putString("link", hotel.REVIEW_URL);
		params.putString("name", hotel.NAME);
		params.putString("caption", "This hotel has got " + hotel.RATING
				+ " rating");
		params.putString("picture", hotel.IMAGE);

		Utility.mAsyncRunner.request("me/feed", params, "POST",
				new RequestListener() {

					public void onMalformedURLException(
							MalformedURLException e, Object state) {
						// TODO Auto-generated method stub

					}

					public void onIOException(IOException e, Object state) {
						// TODO Auto-generated method stub

					}

					public void onFileNotFoundException(
							FileNotFoundException e, Object state) {
						// TODO Auto-generated method stub

					}

					public void onFacebookError(FacebookError e, Object state) {
						// TODO Auto-generated method stub

					}

					public void onComplete(String response, Object state) {
						// TODO Auto-generated method stub
						HotelDetail.this.runOnUiThread(new Runnable() {
							public void run() {
								Toast.makeText(getBaseContext(),
										"Posted on your wall.!",
										Toast.LENGTH_LONG).show();
							}
						});
					}
				}, null);
	}
}
