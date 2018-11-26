package com.example.santiago.bakingapp.Model;

import android.graphics.Bitmap;

/**
 * Created by Santiago on 27/01/2018.
 */

public class Recipe {
    private String mId;
    private String mRecipeName;
    private String mServings;
    private Bitmap mImageBitmap;
    private String imageUrl;

    public Recipe(String mId, String mRecipeName, String mServings, Bitmap mImageBitmap, String imageUrl) {
        this.mId = mId;
        this.mRecipeName = mRecipeName;
        this.mServings = mServings;
        this.mImageBitmap = mImageBitmap;
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return mId;
    }

    public String getRecipeName() {
        return mRecipeName;
    }

    public String getServings() {
        return mServings;
    }

    public Bitmap getImageBitmap() {
        return mImageBitmap;
    }

    public String getImageUrl() {
        return imageUrl;
    }

}
