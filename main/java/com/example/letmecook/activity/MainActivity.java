package com.example.letmecook.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.example.letmecook.R;
import com.example.letmecook.databinding.ActivityMainBinding;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private AppBarConfiguration appBarConfiguration;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Gunakan ViewBinding untuk inflate layout
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        // Atur content view SATU KALI menggunakan root dari binding.
        setContentView(binding.getRoot());

        // EdgeToEdge.enable(this); // EdgeToEdge.enable(this); seringkali tidak diperlukan jika Anda menangani insets secara manual.
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            android.graphics.Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars()).toPlatformInsets();
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set Toolbar sebagai ActionBar Aplikasi
        setSupportActionBar(binding.toolbar);

        // Setup NavController seperti biasa
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_activity_main);

        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();

            Set<Integer> topLevelDestinations = new HashSet<>();
            topLevelDestinations.add(R.id.navigation_homepage);
            topLevelDestinations.add(R.id.navigation_recipe_list);
            topLevelDestinations.add(R.id.navigation_chat);
            topLevelDestinations.add(R.id.navigation_inventory);
            topLevelDestinations.add(R.id.navigation_favorites);

            appBarConfiguration = new AppBarConfiguration.Builder(topLevelDestinations).build();

            // Hubungkan NavController dengan ActionBar yang sudah di-set
            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

            // Hubungkan NavController dengan BottomNavigationView
            NavigationUI.setupWithNavController(binding.navView, navController);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}