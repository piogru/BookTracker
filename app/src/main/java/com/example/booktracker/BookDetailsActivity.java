package com.example.booktracker;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.booktracker.booksearch.BookSearch;
import com.squareup.picasso.Picasso;

public class BookDetailsActivity extends AppCompatActivity {

    public final static String IMAGE_URL_BASE = "http://covers.openlibrary.org/b/id/";
    public final static String EXTRA_BOOK_OBJECT = "EXTRA_BOOK_OBJECT";

    private TextView bookTitleTextView;
    private TextView bookAuthorTextView;
    private ImageView bookCover;
    private TextView bookFirstPublishYearTextView;
    private TextView bookPublishersTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);
        bookTitleTextView = findViewById(R.id.booksearch_title);
        bookAuthorTextView = findViewById(R.id.booksearch_author);

        bookCover = findViewById(R.id.booksearch_img_cover);

        BookSearch book = (BookSearch) getIntent().getSerializableExtra(EXTRA_BOOK_OBJECT);

        bookTitleTextView.setText(book.getTitle());
        bookAuthorTextView.setText(TextUtils.join(", ", book.getAuthors()));

        if (book.getCover() != null) {
            Picasso.with(getApplicationContext())
                    .load(IMAGE_URL_BASE + book.getCover() + "-L.jpg")
                    .placeholder(R.drawable.ic_book_black_24dp).into(bookCover);
        } else {
            bookCover.setImageResource(R.drawable.ic_book_black_24dp);
        }

    }
}