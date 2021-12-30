package com.example.booktracker;

import static com.example.booktracker.AddBooksActivity.EXTRA_BOOK_OBJECT;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.booktracker.booksearch.BookContainer;
import com.example.booktracker.booksearch.BookSearch;
import com.example.booktracker.booksearch.BookService;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookSearchActivity extends AppCompatActivity {

    public static final String IMAGE_URL_BASE = "http://covers.openlibrary.org/b/id/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booksearch);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.booksearch_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.menu_item_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                fetchBooksData(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    private void fetchBooksData(String query) {
        String finalQuery = prepareQuery(query);
        BookService bookService = RetrofitInstance.getRetrofitInstance().create(BookService.class);
        Call<BookContainer> booksApiCall = bookService.findBooks(finalQuery);
        booksApiCall.enqueue((new Callback<BookContainer>() {
            @Override
            public void onResponse(Call<BookContainer> call, Response<BookContainer> response) {
                if (response.code() == 200 && response.body() != null)
                    setupBookListView(response.body().getBookList());
            }

            @Override
            public void onFailure(Call<BookContainer> call, Throwable t) {
                Snackbar.make(findViewById(R.id.main_view), "Something went wrong... Try later",
                        Snackbar.LENGTH_LONG).show();
            }
        }));
    }

    private String prepareQuery(String query) {
        String[] queryParts = query.split("\\s+");
        return TextUtils.join("+", queryParts);
    }

    private void setupBookListView(List<BookSearch> books) {
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        final BookAdapter adapter = new BookAdapter();
        adapter.setBooks(books);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private class BookHolder extends RecyclerView.ViewHolder {


        private TextView bookTitleTextView;
        private TextView bookAuthorTextView;
        private ImageView bookCover;

        public BookHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.booksearch_list_item, parent, false));
            bookTitleTextView = itemView.findViewById(R.id.book_title);
            bookAuthorTextView = itemView.findViewById(R.id.book_author);
            bookCover = itemView.findViewById(R.id.img_cover);
        }

        public void bind(BookSearch book) {
            if(book != null && checkNullOrEmpty(book.getTitle()) && book.getAuthors() != null) {
                bookTitleTextView.setText(book.getTitle());
                bookAuthorTextView.setText(TextUtils.join(", ", book.getAuthors()));

                View itemContainer = itemView.findViewById(R.id.booksearch_item_container);
                itemContainer.setOnClickListener(v -> {
                    Intent intent = new Intent(BookSearchActivity.this, AddBooksActivity.class);
                    intent.putExtra(EXTRA_BOOK_OBJECT, book);
                    activityResultLaunch.launch(intent);
                });

                if(book.getCover() != null) {
                    Picasso.with(itemView.getContext())
                            .load(IMAGE_URL_BASE + book.getCover() + "-S.jpg")
                            .placeholder(R.drawable.ic_book_black_24dp).into(bookCover);
                } else {
                    bookCover.setImageResource(R.drawable.ic_book_black_24dp);
                }
            }


        }

        private boolean checkNullOrEmpty(String s) {
            return s != null && !s.isEmpty();
        }
    }

    ActivityResultLauncher<Intent> activityResultLaunch = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == 100) {
                    Intent replyIntent = new Intent();
                    setResult(100, replyIntent);

                    finish();
                } else if(result.getResultCode() == 110) {
                    Intent replyIntent = new Intent();
                    setResult(110, replyIntent);

                    finish();
                }
            }
        });

    private class BookAdapter extends RecyclerView.Adapter<BookHolder> {
        private List<BookSearch> books;

        @NonNull
        @Override
        public BookHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            return new BookHolder(inflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull BookHolder holder, int position) {
            if (books != null) {
                BookSearch book = books.get(position);
                holder.bind(book);
            } else {
                Log.d("MainActivity", "No books");
            }
        }

        void setBooks(List<BookSearch> books) {
            this.books = books;
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            if(books != null) {
                return books.size();
            } else {
                return 0;
            }
        }
    }
}