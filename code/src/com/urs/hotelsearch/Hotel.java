package com.urs.hotelsearch;

import java.io.Serializable;

public class Hotel implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5204731180952625482L;
	
	// Holds the image URL
	public String IMAGE; 
	
	// Holds the Hotel name
	public String NAME;
	
	// Holds the hotel location
	public String LOCATION;
	
	// Holds hotel rating
	public String RATING;
	
	// Holds hotel number of reviews
	public String REVIEWS;
	
	// Holds hotel review URL
	public String REVIEW_URL;
}
