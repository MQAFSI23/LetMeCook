package com.example.letmecook.network;

import com.example.letmecook.model.CategoryList;
import com.example.letmecook.model.MealList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    @GET("api/json/v1/1/random.php")
    Call<MealList> getRandomMeal();

    @GET("api/json/v1/1/categories.php")
    Call<CategoryList> getCategories();

    @GET("api/json/v1/1/search.php")
    Call<MealList> searchMeals(@Query("s") String query);

    @GET("api/json/v1/1/filter.php")
    Call<MealList> filterByCategory(@Query("c") String category);

    @GET("api/json/v1/1/lookup.php")
    Call<MealList> getMealDetails(@Query("i") String mealId);

}