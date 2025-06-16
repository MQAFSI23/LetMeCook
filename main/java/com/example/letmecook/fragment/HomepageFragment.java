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
import com.example.letmecook.databinding.FragmentHomepageBinding;
import com.example.letmecook.viewmodel.RecipeViewModel;

public class HomepageFragment extends Fragment {

    private FragmentHomepageBinding binding;
    private RecipeViewModel recipeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomepageBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Gunakan ViewModel dari activity agar state terjaga antar fragment
        recipeViewModel = new ViewModelProvider(requireActivity()).get(RecipeViewModel.class);

        // Setup RecyclerView dengan adapter yang sama
        final RecipeAdapter adapter = new RecipeAdapter(new RecipeAdapter.RecipeDiff(), recipe -> recipeViewModel.toggleFavorite(recipe));
        binding.recyclerviewRecommended.setAdapter(adapter);
        // Anda bisa menggunakan LayoutManager lain seperti GridLayoutManager jika diinginkan
        binding.recyclerviewRecommended.setLayoutManager(new LinearLayoutManager(getContext()));

        // Observe data resep yang direkomendasikan
        recipeViewModel.getRecommendedRecipes().observe(getViewLifecycleOwner(), recipes -> {
            if (recipes != null) {
                adapter.submitList(recipes);
                binding.progressbar.setVisibility(View.GONE);
                binding.recyclerviewRecommended.setVisibility(View.VISIBLE);
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