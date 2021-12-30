package com.example.booktracker;

import static com.example.booktracker.MainActivity.IMAGE_URL_BASE;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.booktracker.booksearch.BookSearch;
import com.example.booktracker.database.BookViewModel;
import com.example.booktracker.database.entities.Author;
import com.example.booktracker.database.entities.Book;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AddBookFragment extends Fragment {

    public final static String EXTRA_BOOK_OBJECT = "EXTRA_BOOK_OBJECT";

    private TextView bookTitleTextView;
    private TextView bookAuthorTextView;
    private TextView bookPageCountTextView;
    private TextView bookStartDateTextView;
    private ImageView bookCover;

    private Date startDate;
    private BookViewModel bookViewModel;

    public AddBookFragment() {
        // Required empty public constructor
    }

    public static AddBookFragment newInstance() {
        AddBookFragment fragment = new AddBookFragment();
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
        View view = inflater.inflate(R.layout.fragment_add_book, container, false);
        Activity activity = getActivity();

//        setContentView(R.layout.activity_add_book);
        bookTitleTextView = view.findViewById(R.id.book_title);
        bookAuthorTextView = view.findViewById(R.id.book_author);
        bookPageCountTextView = view.findViewById(R.id.book_page_count);
        bookStartDateTextView = view.findViewById(R.id.book_start_date);

        bookCover = view.findViewById(R.id.book_img_cover);

        BookSearch bookSearch = (BookSearch) activity.getIntent().getSerializableExtra(EXTRA_BOOK_OBJECT);
        startDate = new Date();

        bookTitleTextView.setText(bookSearch.getTitle());
        bookAuthorTextView.setText(TextUtils.join(", ", bookSearch.getAuthors()));
        bookPageCountTextView.setText(bookSearch.getPageCount());
        bookStartDateTextView.setText(startDate.toString());

        bookViewModel = ViewModelProviders.of(this).get(BookViewModel.class);

        if (bookSearch.getCover() != null) {
            Picasso.with(activity.getApplicationContext())
                    .load(IMAGE_URL_BASE + bookSearch.getCover() + "-L.jpg")
                    .placeholder(R.drawable.ic_book_black_24dp).into(bookCover);
        } else {
            bookCover.setImageResource(R.drawable.ic_book_black_24dp);
        }

        Book book = null;

//        LifecycleOwner activity = this;

        final Button buttonSave = view.findViewById(R.id.button_save);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                bookViewModel.findBookWithTitle(bookSearch.getTitle()).observe((LifecycleOwner) activity, new Observer<Book>() {
                    @Override
                    public void onChanged(Book test) {
                        if(test != null) {
                            Intent replyIntent = new Intent();
                            activity.setResult(110, replyIntent);
                            activity.finish();
                        } else {
                            Book book = new Book(bookSearch.getTitle(),
                                    Integer.parseInt(bookSearch.getPageCount()),
                                    startDate, bookSearch.getCover());

                            List<String> authorStrings = bookSearch.getAuthors();
                            List<Author> authors = new ArrayList<Author>();
                            Author author;
                            String[] temp;

                            for(String authorString : authorStrings) {
                                temp = authorString.split(" ", 2);
                                author = new Author(temp[0], temp[1]);
                                authors.add(author);
                            }

                            bookViewModel.insertBookWithAuthors(book, authors);

                            Intent replyIntent = new Intent();
                            activity.setResult(100, replyIntent);

                            activity.finish();
                        }
                    }
                });
            }
        });

        return view;
    }
}