package com.example.letmecook.repository;

import static android.content.ContentValues.TAG;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.letmecook.BuildConfig;
import com.example.letmecook.api.GeminiApiService;
import com.example.letmecook.api.GeminiRequest;
import com.example.letmecook.api.GeminiResponse;
import com.example.letmecook.db.AppDatabase;
import com.example.letmecook.db.dao.InventoryDao;
import com.example.letmecook.db.dao.RecipeDao;
import com.example.letmecook.db.entity.InventoryItem;
import com.example.letmecook.db.entity.Recipe;
import com.example.letmecook.util.CsvParser;

import java.io.InputStream;
import java.util.List;
import java.util.Locale;

import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RecipeRepository {
    private final Application mApplication;
    private final RecipeDao mRecipeDao;
    private final InventoryDao mInventoryDao;
    private final LiveData<List<Recipe>> mAllRecipes;
    private final LiveData<List<Recipe>> mFavoriteRecipes;
    private final LiveData<List<Recipe>> mRecommendedRecipes;
    private final GeminiApiService mGeminiApiService;
    private final MutableLiveData<Recipe> mAiGeneratedRecipe = new MutableLiveData<>();
    private final MutableLiveData<String> mAiError = new MutableLiveData<>();


    public RecipeRepository(Application application) {
        mApplication = application;
        AppDatabase db = AppDatabase.getDatabase(application);
        mRecipeDao = db.recipeDao();
        mInventoryDao = db.inventoryDao();
        mAllRecipes = mRecipeDao.getAllRecipes();
        mFavoriteRecipes = mRecipeDao.getFavoriteRecipes();
        mRecommendedRecipes = mRecipeDao.getRecommendedRecipes();

        checkAndPopulateDb();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://generativelanguage.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mGeminiApiService = retrofit.create(GeminiApiService.class);
    }

    private void checkAndPopulateDb() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            if (mRecipeDao.countRecipes() == 0) {
                // Database kosong, coba isi dari CSV
                Log.w(TAG, "Database is empty. Attempting to populate from CSV.");
                try {
                    InputStream inputStream = mApplication.getAssets().open("recipeList.csv");
                    List<Recipe> recipes = CsvParser.read(inputStream);
                    mRecipeDao.insertAll(recipes);
                    Log.i(TAG, "Successfully populated database from CSV.");
                } catch (Exception e) {
                    Log.e(TAG, "Failed to populate database from CSV on repository check.", e);
                }
            } else {
                Log.i(TAG, "Database already populated.");
            }
        });
    }

    public LiveData<List<Recipe>> getAllRecipes() {
        return mAllRecipes;
    }

    public LiveData<List<Recipe>> getFavoriteRecipes() {
        return mFavoriteRecipes;
    }

    public LiveData<List<Recipe>> getRecommendedRecipes() {
        return mRecommendedRecipes;
    }

    public LiveData<Recipe> getAiGeneratedRecipe() {
        return mAiGeneratedRecipe;
    }

    public LiveData<String> getAiError() {
        return mAiError;
    }

    public void updateRecipe(Recipe recipe) {
        AppDatabase.databaseWriteExecutor.execute(() -> mRecipeDao.update(recipe));
    }

    private void insertRecipe(Recipe recipe) {
        AppDatabase.databaseWriteExecutor.execute(() -> mRecipeDao.insert(recipe));
    }

    public void clearAiRecipe() {
        mAiGeneratedRecipe.postValue(null);
    }

    // Fungsi untuk mengecek ketersediaan bahan
    public String checkIngredientAvailability(Recipe recipe) {
        List<InventoryItem> inventory = mInventoryDao.getAllItemsList(); // Gunakan non-LiveData untuk check cepat
        if (inventory == null || inventory.isEmpty()) {
            return "Tidak Ada Bahan";
        }

        String[] requiredIngredients = recipe.ingredients.toLowerCase(Locale.ROOT).split(",");
        if (requiredIngredients.length == 0) {
            return "Tersedia"; // Tidak butuh bahan
        }

        int availableCount = 0;
        for (String reqIng : requiredIngredients) {
            String cleanReqIng = reqIng.trim();
            for (InventoryItem invItem : inventory) {
                if (cleanReqIng.contains(invItem.itemName.toLowerCase(Locale.ROOT))) {
                    availableCount++;
                    break;
                }
            }
        }

        if (availableCount == 0) {
            return "Tidak Ada Bahan";
        } else if (availableCount < requiredIngredients.length) {
            return "Kurang Bahan";
        } else {
            return "Tersedia";
        }
    }

    // Fungsi untuk memanggil Gemini AI
    public void getRecipeFromAi(String query) {
        GeminiRequest request = getGeminiRequest(query);
        mGeminiApiService.generateContent(BuildConfig.GEMINI_API_KEY, request).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull retrofit2.Call<GeminiResponse> call, @NonNull retrofit2.Response<GeminiResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().candidates != null && !response.body().candidates.isEmpty()) {
                    String textResponse = response.body().candidates.get(0).content.parts.get(0).text;
                    Log.d("GeminiResponse", "Raw text from AI: " + textResponse);

                    if (textResponse.contains("Maaf, saya hanya bisa membantu")) {
                        mAiError.postValue(textResponse);
                        return;
                    }

                    try {
                        // Cari indeks kurung kurawal pembuka dan penutup pertama dan terakhir
                        int startIndex = textResponse.indexOf("{");
                        int endIndex = textResponse.lastIndexOf("}");

                        // Pastikan keduanya ditemukan dan dalam urutan yang benar
                        if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
                            // Ekstrak hanya bagian JSON dari string respons
                            String jsonPart = textResponse.substring(startIndex, endIndex + 1);
                            Log.d("GeminiResponse", "Extracted JSON part: " + jsonPart);

                            // Lakukan parsing HANYA pada bagian JSON yang sudah diekstrak
                            Recipe recipe = new com.google.gson.Gson().fromJson(jsonPart, Recipe.class);
                            recipe.isFavorite = false;

                            // Simpan resep ke database dan update LiveData
                            insertRecipe(recipe);
                            mAiGeneratedRecipe.postValue(recipe);
                        } else {
                            // Jika tidak ditemukan JSON object yang valid dalam respons
                            Log.e("GeminiResponse", "Could not find a valid JSON object in the response.");
                            mAiError.postValue("Respon dari AI tidak dalam format yang benar.");
                        }

                    } catch (com.google.gson.JsonSyntaxException e) {
                        Log.e("GeminiResponse", "JSON parsing error after extraction", e);
                        mAiError.postValue("Gagal memproses resep dari AI. Coba lagi.");
                    }
                } else {
                    mAiError.postValue("Gagal mendapatkan respon dari AI. Kode: " + response.code());
                    Log.e("GeminiResponse", "API call failed with code: " + response.code() + " and message: " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull retrofit2.Call<GeminiResponse> call, @NonNull Throwable t) {
                mAiError.postValue("Terjadi kesalahan jaringan: " + t.getMessage());
                Log.e("GeminiResponse", "Network failure", t);
            }
        });
    }

    @NonNull
    private static GeminiRequest getGeminiRequest(String query) {
        String prompt = "Anda adalah asisten koki bernama LetMeCook. Tugas Anda HANYA memberikan rekomendasi resep masakan. " +
                "Jika pengguna meminta hal lain di luar resep, tolak dengan sopan dengan pesan: 'Maaf, saya hanya bisa membantu dengan resep masakan.' " +
                "Jika pengguna meminta resep, berikan SATU resep berdasarkan '" + query + "'. " +
                "Format balasan Anda HARUS HANYA berupa JSON (tanpa karakter ```json atau lainnya) dengan struktur berikut: " +
                "{\"recipe_name\": \"...\", \"prep_time\": \"...\", \"cook_time\": \"...\", \"servings\": \"...\", \"yield\": \"...\", " +
                "\"ingredients\": \"Bahan 1, Bahan 2, ...\", \"directions\": \"Langkah 1. ..., Langkah 2. ...\", \"rating\": \"4.5\", \"url\": \"\", " +
                "\"img_src\": \"[https://example.com/image.jpg](https://example.com/image.jpg)\"}. Pastikan semua field terisi.";

        return new GeminiRequest(prompt);
    }
}