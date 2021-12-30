package com.example.booktracker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.booktracker.database.BookViewModel;
import com.example.booktracker.database.entities.BookWithAuthors;
import com.google.android.material.snackbar.Snackbar;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.lifecycle.Observer;

public class BooksFragment extends Fragment {

    BookViewModel bookViewModel;
    BookWithAuthors editedBook;

    public BooksFragment() {
    }

    public static BooksFragment newInstance() {
        BooksFragment fragment = new BooksFragment();
//        Bundle args = new Bundle();
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_books, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerview);
        final BookAdapter adapter = new BookAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        bookViewModel = ViewModelProviders.of(this).get(BookViewModel.class);
        bookViewModel.findAllBooksWithAuthors().observe(getViewLifecycleOwner(), new Observer<List<BookWithAuthors>>() {
            @Override
            public void onChanged(List<BookWithAuthors> books) {
                adapter.setBooks(books);
            }
        });

        return view;
    }

    private class BookHolder extends RecyclerView.ViewHolder {
        private TextView bookTitleTextView;
        private TextView bookAuthorTextView;
        private BookWithAuthors book;

        public BookHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.book_list_item, parent, false));

            bookTitleTextView = itemView.findViewById(R.id.book_title);
            bookAuthorTextView = itemView.findViewById(R.id.book_author);
            View bookItem = itemView.findViewById(R.id.book_item);
            bookItem.setOnLongClickListener(v -> {
                bookViewModel.delete(book);
                Snackbar.make(getActivity().findViewById(R.id.coordinator_layout),
                        getString(R.string.book_deleted),
                        Snackbar.LENGTH_LONG)
                        .show();
                return true;
            });
            bookItem.setOnClickListener(v -> {
                editedBook = book;
                Intent intent = new Intent(getActivity(), BookDetailsActivity.class);
                intent.putExtra(BookDetailsActivity.EXTRA_BOOK_TITLE, bookTitleTextView.getText());
                intent.putExtra(BookDetailsActivity.EXTRA_BOOK_AUTHOR, bookAuthorTextView.getText());

                intent.putExtra(BookDetailsActivity.EXTRA_BOOK_PAGE_COUNT, book.book.getPageCount());
                intent.putExtra(BookDetailsActivity.EXTRA_BOOK_START_DATE, book.book.getStartDate().getTime());
                if(book.book.getEndDate() != null){
                    intent.putExtra(BookDetailsActivity.EXTRA_BOOK_END_DATE, book.book.getEndDate().getTime());
                }
                intent.putExtra(BookDetailsActivity.EXTRA_BOOK_TIME_SPENT, book.book.getTimeSpent());
                intent.putExtra(BookDetailsActivity.EXTRA_BOOK_COVER, book.book.getCover());

                activityResultLaunch.launch(intent);
            });
        }

        public void bind(BookWithAuthors book) {
            bookTitleTextView.setText(book.book.getTitle());
            bookAuthorTextView.setText(TextUtils.join(", ", book.authors));
            this.book = book;
        }
    }

        ActivityResultLauncher<Intent> activityResultLaunch = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                Activity activity = getActivity();

                if (result.getResultCode() == 100) {
                    Snackbar.make(activity.findViewById(R.id.coordinator_layout), getString(R.string.book_added),
                            Snackbar.LENGTH_LONG).show();
                } else if (result.getResultCode() == 110) {
                    Snackbar.make(activity.findViewById(R.id.coordinator_layout), getString(R.string.book_not_added),
                            Snackbar.LENGTH_LONG).show();
                } else if (result.getResultCode() == 200) {
                    Date endDate = new Date();
                    long diffInMillies = Math.abs(endDate.getTime() - editedBook.book.getStartDate().getTime());
                    long diff = TimeUnit.MINUTES.convert(diffInMillies, TimeUnit.MILLISECONDS);
                    int timeSpent = (int)diff;
                    editedBook.book.setEndDate(endDate);
                    editedBook.book.setTimeSpent(timeSpent);
                    bookViewModel.update(editedBook.book);
                    Snackbar.make(activity.findViewById(R.id.coordinator_layout), getString(R.string.book_finished),
                            Snackbar.LENGTH_LONG).show();
                }
            }
        });

    private class BookAdapter extends RecyclerView.Adapter<BookHolder> {
        private List<BookWithAuthors> books;

        @NonNull
        @Override
        public BookHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new BookHolder(getLayoutInflater(), parent);
        }

        @Override
        public void onBindViewHolder(@NonNull BookHolder holder, int position) {
            if (books != null) {
                BookWithAuthors book = books.get(position);
                holder.bind(book);
            } else {
                Log.d("MainActivity", "No books");
            }
        }

        @Override
        public int getItemCount() {
            if(books != null) {
                return books.size();
            } else {
                return 0;
            }
        }

        void setBooks(List<BookWithAuthors> books) {
            this.books = books;
            notifyDataSetChanged();
        }

    }
}