package com.example.booktracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.booktracker.database.BookViewModel;
import com.example.booktracker.database.entities.BookWithAuthors;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    public static final String IMAGE_URL_BASE = "http://covers.openlibrary.org/b/id/";
    public static final String KEY_CURRENT_FRAGMENT = "current_fragment";

    Fragment currentFragment;
    BottomNavigationView bottomNavigation;

    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigation = findViewById(R.id.bottom_navigation);

        bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.page_books:
                        currentFragment = BooksFragment.newInstance();
                        openFragment(currentFragment);
                        return true;

                    case R.id.page_new:
                        currentFragment = BookSearchFragment.newInstance();
                        openFragment(currentFragment);
                        return true;
                    case R.id.page_statistics:
                        currentFragment = StatisticsFragment.newInstance();
                        openFragment(currentFragment);
                        return true;
                }
                return false;
            }
        });

        if(savedInstanceState != null) {
            currentFragment = getSupportFragmentManager().getFragment(savedInstanceState, KEY_CURRENT_FRAGMENT);
            openFragment(currentFragment);
        } else {
            openFragment(BooksFragment.newInstance());
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //Save the fragment's instance
        currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if(currentFragment != null) {
            getSupportFragmentManager().putFragment(outState, KEY_CURRENT_FRAGMENT, currentFragment);
        }
    }
}

