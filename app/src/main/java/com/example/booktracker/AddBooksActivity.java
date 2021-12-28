package com.example.booktracker;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.booktracker.book.Book;
import com.example.booktracker.book.BookViewModel;
import com.example.booktracker.booksearch.BookSearch;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.List;

public class AddBooksActivity extends AppCompatActivity {

    public final static String IMAGE_URL_BASE = "http://covers.openlibrary.org/b/id/";
    public final static String EXTRA_BOOK_OBJECT = "EXTRA_BOOK_OBJECT";

    private TextView bookTitleTextView;
    private TextView bookAuthorTextView;
    private TextView bookPageCountTextView;
    private TextView bookStartDateTextView;
    private ImageView bookCover;

    private Date startDate;
    private BookViewModel bookViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);
        bookTitleTextView = findViewById(R.id.book_title);
        bookAuthorTextView = findViewById(R.id.book_author);
        bookPageCountTextView = findViewById(R.id.book_page_count);
        bookStartDateTextView = findViewById(R.id.book_start_date);

        bookCover = findViewById(R.id.book_img_cover);

        BookSearch bookSearch = (BookSearch) getIntent().getSerializableExtra(EXTRA_BOOK_OBJECT);
        startDate = new Date();

        bookTitleTextView.setText(bookSearch.getTitle());
        bookAuthorTextView.setText(TextUtils.join(", ", bookSearch.getAuthors()));
        bookPageCountTextView.setText(bookSearch.getPageCount());
        bookStartDateTextView.setText(startDate.toString());

        bookViewModel = ViewModelProviders.of(this).get(BookViewModel.class);

        if (bookSearch.getCover() != null) {
            Picasso.with(getApplicationContext())
                    .load(IMAGE_URL_BASE + bookSearch.getCover() + "-L.jpg")
                    .placeholder(R.drawable.ic_book_black_24dp).into(bookCover);
        } else {
            bookCover.setImageResource(R.drawable.ic_book_black_24dp);
        }

        final Button buttonSave = findViewById(R.id.button_save);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Book book = new Book(bookSearch.getTitle(), "static author",
                        Integer.parseInt(bookSearch.getPageCount()), startDate, bookSearch.getCover());
                bookViewModel.insert(book);

                Intent replyIntent = new Intent();
                setResult(100, replyIntent);

                finish();
            }
        });
    }
}