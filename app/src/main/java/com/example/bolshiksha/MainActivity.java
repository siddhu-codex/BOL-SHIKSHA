package com.example.bolshiksha;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;

import com.example.bolshiksha.ui.auth.AuthSession;
import com.example.bolshiksha.ui.auth.LoginActivity;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.navigation.fragment.NavHostFragment;

import com.example.bolshiksha.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!AuthSession.isSignedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        EdgeToEdge.enable(this);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setSupportActionBar(binding.toolbar);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_content_main);

        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();

            appBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.dashboardFragment,
                    R.id.translationFragment,
                    R.id.voiceTranslationLandingFragment,
                    R.id.curriculumFragment,
                    R.id.worksheetsFragment,
                    R.id.flashcardsFragment,
                    R.id.settingsFragment
            ).build();
            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        }

        binding.fab.setOnClickListener(view -> {
            if (navController != null && navController.getCurrentDestination() != null) {
                int id = navController.getCurrentDestination().getId();
                if (id == R.id.dashboardFragment) {
                    navController.navigate(R.id.action_dashboard_to_voiceTranslation);
                } else {
                    Snackbar.make(view, "⌨ हिन्दी टेक्स्ट से अनुवाद करें / Translate Hindi text",
                            Snackbar.LENGTH_LONG)
                            .setAnchorView(R.id.fab)
                            .setAction("अनुवाद / Translate", v -> {
                                if (navController != null) {
                                    if (navController.getCurrentDestination() != null
                                            && navController.getCurrentDestination().getId()
                                            != R.id.translationFragment) {
                                        navController.navigate(R.id.translationFragment);
                                    }
                                }
                            })
                            .show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            if (navController != null && navController.getCurrentDestination() != null
                    && navController.getCurrentDestination().getId() != R.id.settingsFragment) {
                navController.navigate(R.id.settingsFragment);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_content_main);
        boolean handled = false;
        if (navHostFragment != null) {
            NavController nc = navHostFragment.getNavController();
            handled = NavigationUI.navigateUp(nc, appBarConfiguration);
        }
        return handled || super.onSupportNavigateUp();
    }
}
