package com.example.moodscape.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.moodscape.R;
import com.example.moodscape.fragments.*;

public class HomeActivity extends AppCompatActivity {
    BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bottomNav = findViewById(R.id.bottomNav);
        loadFragment(new HomeFragment());

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            int id = item.getItemId();
            if (id == R.id.nav_home) fragment = new HomeFragment();
            else if (id == R.id.nav_diary) fragment = new DiaryFragment();
            else if (id == R.id.nav_chat) fragment = new ChatbotFragment();
            else if (id == R.id.nav_activities) fragment = new ActivitiesFragment();
            else if (id == R.id.nav_profile) fragment = new ProfileFragment();
            if (fragment != null) { loadFragment(fragment); return true; }
            return false;
        });
        bottomNav.setSelectedItemId(R.id.nav_home);
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
}