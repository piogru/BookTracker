package com.example.booktracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import static com.example.booktracker.AddBooksActivity.EXTRA_BOOK_OBJECT;
import static com.example.booktracker.MainActivity.IMAGE_URL_BASE;

import com.example.booktracker.booksearch.BookContainer;
import com.example.booktracker.booksearch.BookSearch;
import com.example.booktracker.booksearch.BookService;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookSearchFragment extends Fragment {

    SearchView searchView;

    public BookSearchFragment() {
        // Required empty public constructor
    }

    public static BookSearchFragment newInstance() {
        BookSearchFragment fragment = new BookSearchFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_book_search, container, false);

        searchView = view.findViewById(R.id.book_search_bar);
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

        return view;
    }

    private void fetchBooksData(String query) {
        View view = getView();
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
                Snackbar.make(view.findViewById(R.id.main_view), getResources().getText(R.string.book_search_failed),
                        Snackbar.LENGTH_LONG).show();
            }
        }));
    }

    private String prepareQuery(String query) {
        String[] queryParts = query.split("\\s+");
        return TextUtils.join("+", queryParts);
    }

    private void setupBookListView(List<BookSearch> books) {
        View view = getView();
        RecyclerView recyclerView = view.findViewById(R.id.recyclerview);
        final BookAdapter adapter = new BookAdapter();
        adapter.setBooks(books);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    ActivityResultLauncher<Intent> activityResultLaunch = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                Activity activity = getActivity();
                if (result.getResultCode() == 100) {
                    Intent replyIntent = new Intent();
                    activity.setResult(100, replyIntent);
                    BottomNavigationView bottomNavigation = activity.findViewById(R.id.bottom_navigation);

                    bottomNavigation.setSelectedItemId(R.id.page_books);
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new BooksFragment())
                            .addToBackStack(null)
                            .commit();

                    Snackbar.make(getActivity().findViewById(R.id.coordinator_layout),
                            getString(R.string.book_added),
                            Snackbar.LENGTH_LONG)
                            .show();
                } else if(result.getResultCode() == 110) {
                    Intent replyIntent = new Intent();
                    activity.setResult(110, replyIntent);

                    BottomNavigationView bottomNavigation = activity.findViewById(R.id.bottom_navigation);
                    bottomNavigation.setSelectedItemId(R.id.page_books);
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new BooksFragment())
                            .addToBackStack(null)
                            .commit();

                    Snackbar.make(getActivity().findViewById(R.id.coordinator_layout),
                            getString(R.string.book_not_added),
                            Snackbar.LENGTH_LONG)
                            .show();
                }
            }
        });

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
                    Intent intent = new Intent(getActivity(), AddBooksActivity.class);
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