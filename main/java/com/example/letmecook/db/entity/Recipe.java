package com.example.letmecook.db.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

@Entity(tableName = "recipes")
public class Recipe implements Serializable {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @SerializedName("recipe_name")
    public String recipeName;

    @SerializedName("prep_time")
    public String prepTime;

    @SerializedName("cook_time")
    public String cookTime;

    @SerializedName("servings")
    public String servings;

    @SerializedName("yield")
    public String yield;

    @SerializedName("ingredients")
    public String ingredients;

    @SerializedName("directions")
    public String directions;

    @SerializedName("rating")
    public String rating;

    @SerializedName("url")
    public String url;

    @SerializedName("img_src")
    public String imgSrc;

    public boolean isFavorite = false;

    public transient String availabilityStatus = "Mengecek...";
}