package com.example.letmecook.model;

import com.google.gson.annotations.SerializedName;

public class Category {
    @SerializedName("strCategory")
    private String strCategory;

    public String getStrCategory() { return strCategory; }
}