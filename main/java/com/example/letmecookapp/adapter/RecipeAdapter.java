package com.example.letmecookapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.letmecookapp.model.Meal;
import com.example.letmecookapp.R;
import com.example.letmecookapp.RecipeDetailActivity;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {
    private Context context;
    private List<Meal> mealList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Meal meal);
    }

    public RecipeAdapter(Context context, List<Meal> mealList, OnItemClickListener listener) {
        this.context = context;
        this.mealList = mealList;
        this.listener = listener; // Listener ini sekarang akan diimplementasikan di HomeFragment
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recipe, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Meal meal = mealList.get(position);
        holder.bind(meal, listener);
    }

    @Override
    public int getItemCount() {
        return mealList == null ? 0 : mealList.size();
    }

    public void updateData(List<Meal> newMealList) {
        this.mealList = newMealList;
        notifyDataSetChanged(); // Atau gunakan DiffUtil untuk performa lebih baik
    }

    static class RecipeViewHolder extends RecyclerView.ViewHolder {
        ImageView imageRecipe;
        TextView textRecipeName, textRecipeArea;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            imageRecipe = itemView.findViewById(R.id.image_recipe);
            textRecipeName = itemView.findViewById(R.id.text_recipe_name);
            textRecipeArea = itemView.findViewById(R.id.text_recipe_area);
        }

        public void bind(final Meal meal, final OnItemClickListener listener) {
            textRecipeName.setText(meal.getStrMeal());
            textRecipeArea.setText(meal.getStrArea());

            Glide.with(itemView.getContext())
                    .load(meal.getStrMealThumb())
                    .placeholder(R.drawable.placeholder_food)
                    .error(R.drawable.placeholder_food)
                    .into(imageRecipe);

            itemView.setOnClickListener(v -> {
                // Hapus NavController, ganti dengan Intent
                Intent intent = new Intent(itemView.getContext(), RecipeDetailActivity.class);
                intent.putExtra(RecipeDetailActivity.EXTRA_MEAL_ID, meal.getIdMeal());
                intent.putExtra(RecipeDetailActivity.EXTRA_MEAL_NAME, meal.getStrMeal());
                itemView.getContext().startActivity(intent);
            });
        }
    }
}