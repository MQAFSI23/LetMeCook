package com.example.letmecook.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.example.letmecook.db.entity.Recipe;
import java.util.List;

@Dao
public interface RecipeDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<Recipe> recipes);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Recipe recipe);

    @Update
    void update(Recipe recipe);

    @Query("SELECT * FROM recipes ORDER BY recipeName ASC")
    LiveData<List<Recipe>> getAllRecipes();

    @Query("SELECT * FROM recipes WHERE isFavorite = 1 ORDER BY recipeName ASC")
    LiveData<List<Recipe>> getFavoriteRecipes();

    @Query("SELECT * FROM recipes ORDER BY RANDOM() LIMIT 5")
    LiveData<List<Recipe>> getRecommendedRecipes();

    @Query("SELECT COUNT(*) FROM recipes")
    int countRecipes();
}