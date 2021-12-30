package com.example.booktracker;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.booktracker.database.AuthorViewModel;
import com.example.booktracker.database.BookAuthorRepository;
import com.example.booktracker.database.entities.Author;
import com.example.booktracker.database.entities.Book;
import com.example.booktracker.database.BookViewModel;
import com.example.booktracker.database.entities.BookAuthorCrossRef;
import com.example.booktracker.database.entities.BookWithAuthors;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.snackbar.Snackbar;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    public static final String IMAGE_URL_BASE = "http://covers.openlibrary.org/b/id/";

    BottomNavigationView bottomNavigation;

    private BookViewModel bookViewModel;
    private BookWithAuthors editedBook;

    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
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
                        openFragment(BooksFragment.newInstance());
                        return true;
                    case R.id.page_new:
//                        Intent intent = new Intent(MainActivity.this, BookSearchActivity.class);
//                        activityResultLaunch.launch(intent);
//                        return true;
                        openFragment(BookSearchFragment.newInstance());
                        return true;
                    case R.id.page_statistics:
//                        openFragment(StatisticsFragment.newInstance());
                        return true;

                }
                return false;
            }
        });

        openFragment(BooksFragment.newInstance());
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

//    @Override
//    public boolean onSupportNavigateUp() {
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
//        return NavigationUI.navigateUp(navController, appBarConfiguration)
//                || super.onSupportNavigateUp();
//    }

//    ActivityResultLauncher<Intent> activityResultLaunch = registerForActivityResult(
//        new ActivityResultContracts.StartActivityForResult(),
//        new ActivityResultCallback<ActivityResult>() {
//            @Override
//            public void onActivityResult(ActivityResult result) {
//                if (result.getResultCode() == 100) {
//                    Snackbar.make(findViewById(R.id.coordinator_layout), getString(R.string.book_added),
//                            Snackbar.LENGTH_LONG).show();
//                } else if (result.getResultCode() == 110) {
//                    Snackbar.make(findViewById(R.id.coordinator_layout), getString(R.string.book_not_added),
//                            Snackbar.LENGTH_LONG).show();
//                } else if (result.getResultCode() == 200) {
//                    Date endDate = new Date();
//                    long diffInMillies = Math.abs(endDate.getTime() - editedBook.book.getStartDate().getTime());
//                    long diff = TimeUnit.MINUTES.convert(diffInMillies, TimeUnit.MILLISECONDS);
//                    int timeSpent = (int)diff;
//                    editedBook.book.setEndDate(endDate);
//                    editedBook.book.setTimeSpent(timeSpent);
//                    bookViewModel.update(editedBook.book);
//                    Snackbar.make(findViewById(R.id.coordinator_layout), getString(R.string.book_finished),
//                            Snackbar.LENGTH_LONG).show();
//                }
//            }
//        });
}

