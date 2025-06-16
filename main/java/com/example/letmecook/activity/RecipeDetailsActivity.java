package com.example.letmecook.activity;

import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.bumptech.glide.Glide;
import com.example.letmecook.R;
import com.example.letmecook.databinding.ActivityRecipeDetailsBinding;
import com.example.letmecook.db.entity.Recipe;

public class RecipeDetailsActivity extends AppCompatActivity {

    public static final String EXTRA_RECIPE = "com.example.letmecook.EXTRA_RECIPE";
    private ActivityRecipeDetailsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRecipeDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Setup Toolbar
        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Get Recipe from Intent
        Recipe recipe = (Recipe) getIntent().getSerializableExtra(EXTRA_RECIPE);

        if (recipe != null) {
            populateUI(recipe);
        }
    }

    private void populateUI(Recipe recipe) {
        // Set Judul di CollapsingToolbar
        binding.toolbarLayout.setTitle(recipe.recipeName);

        // Load Gambar
        Glide.with(this)
                .load(recipe.imgSrc)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.placeholder_image)
                .into(binding.imageRecipeDetail);

        // Format dan tampilkan bahan
        binding.textIngredientsContent.setText(formatList(recipe.ingredients, ","));

        // Format dan tampilkan cara memasak
        binding.textDirectionsContent.setText(formatList(recipe.directions, "\\."));
    }

    /**
     * Mengubah string yang dipisahkan oleh delimiter menjadi daftar HTML berformat.
     * @param content String konten (e.g., "Bahan 1, Bahan 2")
     * @param delimiter Regex untuk memisahkan string (e.g., "," atau "\\.")
     * @return Spanned object untuk ditampilkan di TextView.
     */
    private Spanned formatList(String content, String delimiter) {
        if (content == null || content.isEmpty()) {
            return Html.fromHtml("", Html.FROM_HTML_MODE_LEGACY);
        }

        StringBuilder htmlList = new StringBuilder();
        String[] items = content.split(delimiter);

        for (String item : items) {
            String trimmedItem = item.trim();
            if (!trimmedItem.isEmpty()) {
                // Untuk bahan, gunakan bullet point. Untuk cara memasak, tambahkan nomor.
                if (delimiter.equals(",")) {
                    htmlList.append("&#8226; ").append(trimmedItem).append("<br/>");
                } else {
                    htmlList.append(trimmedItem).append(".<br/><br/>");
                }
            }
        }

        return Html.fromHtml(htmlList.toString(), Html.FROM_HTML_MODE_LEGACY);
    }

    // Handle klik tombol kembali di toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}