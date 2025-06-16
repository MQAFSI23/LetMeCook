package com.example.letmecook.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.letmecook.adapter.RecipeAdapter;
import com.example.letmecook.databinding.FragmentFavoritesBinding;
import com.example.letmecook.viewmodel.RecipeViewModel;

public class FavoritesFragment extends Fragment {

    private FragmentFavoritesBinding binding;
    private RecipeViewModel recipeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recipeViewModel = new ViewModelProvider(requireActivity()).get(RecipeViewModel.class);

        final RecipeAdapter adapter = new RecipeAdapter(new RecipeAdapter.RecipeDiff(), recipe -> recipeViewModel.toggleFavorite(recipe));
        binding.recyclerviewFavorites.setAdapter(adapter);
        binding.recyclerviewFavorites.setLayoutManager(new LinearLayoutManager(getContext()));

        // Observe data resep favorit
        recipeViewModel.getFavoriteRecipes().observe(getViewLifecycleOwner(), recipes -> {
            if (recipes != null && !recipes.isEmpty()) {
                adapter.submitList(recipes);
                binding.recyclerviewFavorites.setVisibility(View.VISIBLE);
                binding.textEmptyState.setVisibility(View.GONE);
            } else {
                // Tampilkan pesan jika tidak ada resep favorit
                binding.recyclerviewFavorites.setVisibility(View.GONE);
                binding.textEmptyState.setVisibility(View.VISIBLE);
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}