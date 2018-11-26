package com.example.santiago.bakingapp.Model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Santiago on 27/01/2018.
 */

public class Ingredient implements Parcelable {
    private String mQuantity;
    private String mMeasure;
    private String mIngredient;

    public Ingredient(String quantity, String measure, String ingredient) {
        mQuantity = quantity;
        mMeasure = measure;
        mIngredient = ingredient;
    }

    public String getQuantity() {
        return mQuantity;
    }

    public String getMeasure() {
        return mMeasure;
    }

    public String getIngredient() {
        return mIngredient;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mQuantity);
        dest.writeString(this.mMeasure);
        dest.writeString(this.mIngredient);
    }

    protected Ingredient(Parcel in) {
        this.mQuantity = in.readString();
        this.mMeasure = in.readString();
        this.mIngredient = in.readString();
    }

    public static final Parcelable.Creator<Ingredient> CREATOR = new Parcelable.Creator<Ingredient>() {
        @Override
        public Ingredient createFromParcel(Parcel source) {
            return new Ingredient(source);
        }

        @Override
        public Ingredient[] newArray(int size) {
            return new Ingredient[size];
        }
    };
}
