package com.example.letmecook.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class MealList {
    @SerializedName("meals")
    private List<Meal> meals;

    public List<Meal> getMeals() {
        return meals;
    }
}