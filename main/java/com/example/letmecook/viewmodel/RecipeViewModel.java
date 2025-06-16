package com.example.letmecook.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.example.letmecook.db.entity.Recipe;
import com.example.letmecook.repository.RecipeRepository;

import java.util.List;
import java.util.concurrent.Executors;

public class RecipeViewModel extends AndroidViewModel {

    private final RecipeRepository mRepository;
    private final MediatorLiveData<List<Recipe>> mAllRecipesWithStatus = new MediatorLiveData<>();
    private final MediatorLiveData<List<Recipe>> mFavoriteRecipesWithStatus = new MediatorLiveData<>();
    private final MediatorLiveData<List<Recipe>> mRecommendedRecipesWithStatus = new MediatorLiveData<>();

    public RecipeViewModel(Application application) {
        super(application);
        mRepository = new RecipeRepository(application);

        // Menggabungkan data resep dengan status ketersediaan
        mAllRecipesWithStatus.addSource(mRepository.getAllRecipes(), recipes ->
                updateRecipeStatus(recipes, mAllRecipesWithStatus));

        mFavoriteRecipesWithStatus.addSource(mRepository.getFavoriteRecipes(), recipes ->
                updateRecipeStatus(recipes, mFavoriteRecipesWithStatus));

        mRecommendedRecipesWithStatus.addSource(mRepository.getRecommendedRecipes(), recipes ->
                updateRecipeStatus(recipes, mRecommendedRecipesWithStatus));
    }

    private void updateRecipeStatus(List<Recipe> recipes, MediatorLiveData<List<Recipe>> liveData) {
        if (recipes != null) {
            Executors.newSingleThreadExecutor().execute(() -> {
                for (Recipe recipe : recipes) {
                    recipe.availabilityStatus = mRepository.checkIngredientAvailability(recipe);
                }
                liveData.postValue(recipes);
            });
        }
    }

    public LiveData<List<Recipe>> getAllRecipes() {
        return mAllRecipesWithStatus;
    }

    public LiveData<List<Recipe>> getFavoriteRecipes() {
        return mFavoriteRecipesWithStatus;
    }

    public LiveData<List<Recipe>> getRecommendedRecipes() {
        return mRecommendedRecipesWithStatus;
    }

    public LiveData<Recipe> getAiGeneratedRecipe() {
        return mRepository.getAiGeneratedRecipe();
    }

    public LiveData<String> getAiError() {
        return mRepository.getAiError();
    }

    public void toggleFavorite(Recipe recipe) {
        recipe.isFavorite = !recipe.isFavorite;
        mRepository.updateRecipe(recipe);
    }

    public void getRecipeFromAi(String query) {
        mRepository.getRecipeFromAi(query);
    }

    public void onAiRecipeConsumed() {
        mRepository.clearAiRecipe();
    }
}