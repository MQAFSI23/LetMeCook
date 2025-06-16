package com.example.letmecook.db;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.letmecook.db.dao.InventoryDao;
import com.example.letmecook.db.dao.RecipeDao;
import com.example.letmecook.db.entity.InventoryItem;
import com.example.letmecook.db.entity.Recipe;
import com.example.letmecook.util.CsvParser;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Recipe.class, InventoryItem.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract RecipeDao recipeDao();
    public abstract InventoryDao inventoryDao();

    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "letmecook_database")
                            .addCallback(new RoomDatabase.Callback() {
                                @Override
                                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                    super.onCreate(db);
                                    databaseWriteExecutor.execute(() -> {
                                        // Isi database dari CSV saat pertama kali dibuat
                                        try {
                                            InputStream inputStream = context.getAssets().open("recipeList.csv");
                                            List<Recipe> recipes = CsvParser.read(inputStream);
                                            getDatabase(context).recipeDao().insertAll(recipes);
                                            Log.d("AppDatabase", "Successfully populated recipes from CSV.");
                                        } catch (IOException e) {
                                            Log.e("AppDatabase", "Error reading CSV", e);
                                        }
                                    });
                                }
                            })
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}