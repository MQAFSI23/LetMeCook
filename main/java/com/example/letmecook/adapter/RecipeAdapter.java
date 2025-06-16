package com.example.letmecook.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.letmecook.R;
import com.example.letmecook.activity.RecipeDetailsActivity;
import com.example.letmecook.databinding.ItemRecipeCardBinding;
import com.example.letmecook.db.entity.Recipe;

import java.util.Objects;

public class RecipeAdapter extends ListAdapter<Recipe, RecipeAdapter.RecipeViewHolder> {

    private final OnFavoriteClickListener favoriteClickListener;

    public interface OnFavoriteClickListener {
        void onFavoriteClick(Recipe recipe);
    }

    public RecipeAdapter(@NonNull DiffUtil.ItemCallback<Recipe> diffCallback, OnFavoriteClickListener listener) {
        super(diffCallback);
        this.favoriteClickListener = listener;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRecipeCardBinding binding = ItemRecipeCardBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new RecipeViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe current = getItem(position);
        holder.bind(current, favoriteClickListener);
    }

    static class RecipeViewHolder extends RecyclerView.ViewHolder {
        private final ItemRecipeCardBinding binding;
        private final Context context;

        public RecipeViewHolder(ItemRecipeCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            this.context = itemView.getContext();
        }

        public void bind(Recipe recipe, OnFavoriteClickListener listener) {
            binding.textRecipeName.setText(recipe.recipeName);

            // Set Favorite Icon
            if (recipe.isFavorite) {
                binding.buttonFavorite.setImageResource(R.drawable.ic_favorite_filled);
            } else {
                binding.buttonFavorite.setImageResource(R.drawable.ic_favorite_border);
            }
            binding.buttonFavorite.setOnClickListener(v -> listener.onFavoriteClick(recipe));

            // Set Availability Status
            binding.textAvailabilityStatus.setText(recipe.availabilityStatus);
            setAvailabilityStatusColor(recipe.availabilityStatus);

            // Load Image
            Glide.with(context)
                    .load(recipe.imgSrc)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.placeholder_image)
                    .into(binding.imageRecipe);

            // Details Button
            binding.buttonDetails.setOnClickListener(v -> {
                Intent intent = new Intent(context, RecipeDetailsActivity.class);
                intent.putExtra(RecipeDetailsActivity.EXTRA_RECIPE, recipe);
                context.startActivity(intent);
            });
        }

        private void setAvailabilityStatusColor(String status) {
            int colorRes;
            switch (status) {
                case "Tersedia":
                    colorRes = R.color.status_available;
                    break;
                case "Kurang Bahan":
                    colorRes = R.color.status_partial;
                    break;
                case "Tidak Ada Bahan":
                default:
                    colorRes = R.color.status_unavailable;
                    break;
            }
            GradientDrawable background = (GradientDrawable) binding.textAvailabilityStatus.getBackground();
            background.setColor(ContextCompat.getColor(context, colorRes));
        }
    }

    public static class RecipeDiff extends DiffUtil.ItemCallback<Recipe> {
        @Override
        public boolean areItemsTheSame(@NonNull Recipe oldItem, @NonNull Recipe newItem) {
            return oldItem.id == newItem.id;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Recipe oldItem, @NonNull Recipe newItem) {
            return Objects.equals(oldItem.recipeName, newItem.recipeName)
                    && oldItem.isFavorite == newItem.isFavorite
                    && Objects.equals(oldItem.availabilityStatus, newItem.availabilityStatus);
        }
    }
}