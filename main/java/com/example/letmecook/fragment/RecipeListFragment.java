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
import com.example.letmecook.databinding.FragmentRecipeListBinding;
import com.example.letmecook.viewmodel.RecipeViewModel;

public class RecipeListFragment extends Fragment {

    private FragmentRecipeListBinding binding;
    private RecipeViewModel recipeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRecipeListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Setup ViewModel
        recipeViewModel = new ViewModelProvider(this).get(RecipeViewModel.class);

        // Setup RecyclerView
        final RecipeAdapter adapter = new RecipeAdapter(new RecipeAdapter.RecipeDiff(), recipe -> recipeViewModel.toggleFavorite(recipe));
        binding.recyclerviewRecipes.setAdapter(adapter);
        binding.recyclerviewRecipes.setLayoutManager(new LinearLayoutManager(getContext()));

        // Observe data
        // Update the cached copy of the words in the adapter.
        recipeViewModel.getAllRecipes().observe(getViewLifecycleOwner(), adapter::submitList);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}