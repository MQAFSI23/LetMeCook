package com.example.letmecook.util;

import android.util.Log;

import com.example.letmecook.db.entity.Recipe;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CsvParser {

    private static final String TAG = "CsvParser";

    public static List<Recipe> read(InputStream inputStream) {
        List<Recipe> recipes = new ArrayList<>();
        // Gunakan try-with-resources untuk memastikan reader ditutup secara otomatis
        try (CSVReader reader = new CSVReader(new InputStreamReader(inputStream))) {
            // Lewati baris header
            reader.skip(1);

            List<String[]> allRows = reader.readAll();
            for (String[] row : allRows) {
                try {
                    Recipe recipe = new Recipe();
                    // Indeks kolom berdasarkan file recipeList.csv
                    // recipe_name,prep_time,cook_time,servings,yield,ingredients,directions,rating,url,img_src
                    recipe.recipeName = row[0];
                    recipe.prepTime = row[1];
                    recipe.cookTime = row[2];
                    recipe.servings = row[3];
                    recipe.yield = row[4];
                    recipe.ingredients = row[5];
                    recipe.directions = row[6];
                    recipe.rating = row[7];
                    recipe.url = row[8];
                    recipe.imgSrc = row[9];
                    recipe.isFavorite = false; // Nilai default

                    recipes.add(recipe);
                } catch (ArrayIndexOutOfBoundsException e) {
                    Log.e(TAG, "Skipping malformed row: Not enough columns.", e);
                }
            }
        } catch (IOException | CsvException e) {
            Log.e(TAG, "Error reading CSV file", e);
        }

        Log.d(TAG, "Successfully parsed " + recipes.size() + " recipes.");
        return recipes;
    }
}