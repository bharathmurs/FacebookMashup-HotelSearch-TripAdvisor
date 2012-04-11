package com.urs.hotelsearch;

import android.graphics.Bitmap;

public class CustomListView {
	// Holds the icon id.
    public int icon;
    
    // Holds the cell title
    public String title;
    
    // Holds the cell image bitmap.
    public Bitmap bmp;
    
    public CustomListView(){
        super();
    }
    
    public CustomListView(int icon, String title) {
        super();
        this.icon = icon;
        this.title = title;
    }
    
    public CustomListView(String url, String title) {
        super();
        this.bmp = Utility.getImageBitmap(url);
        this.title = title;
    }
}
