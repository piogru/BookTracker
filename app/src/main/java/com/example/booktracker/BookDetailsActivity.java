package com.example.booktracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.booktracker.booksearch.BookSearch;
import com.example.booktracker.database.BookViewModel;
import com.squareup.picasso.Picasso;

import java.util.Date;

import static com.example.booktracker.MainActivity.IMAGE_URL_BASE;

public class BookDetailsActivity extends AppCompatActivity {

    public static final String EXTRA_BOOK_TITLE = "com.example.booktracker.EDIT_BOOK_TITLE";
    public static final String EXTRA_BOOK_AUTHOR = "com.example.booktracker.EDIT_BOOK_AUTHOR";
    public static final String EXTRA_BOOK_PAGE_COUNT = "com.example.booktracker.EDIT_BOOK_PAGE_COUNT";
    public static final String EXTRA_BOOK_START_DATE = "com.example.booktracker.EDIT_BOOK_START_DATE";
    public static final String EXTRA_BOOK_END_DATE = "com.example.booktracker.EDIT_BOOK_END_DATE";
    public static final String EXTRA_BOOK_TIME_SPENT = "com.example.booktracker.EDIT_BOOK_TIME_SPENT";
    public static final String EXTRA_BOOK_COVER = "com.example.booktracker.EDIT_BOOK_COVER";

    private TextView titleTextView;
    private TextView authorTextView;
    private TextView pageCountTextView;
    private TextView startDateTextView;
    private TextView endDateTextView;
    private TextView timeSpentTextView;
    private ImageView bookCover;

    private BookViewModel bookViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);

        titleTextView = findViewById(R.id.book_title);
        authorTextView = findViewById(R.id.book_author);
        pageCountTextView = findViewById(R.id.book_page_count);
        startDateTextView = findViewById(R.id.book_start_date);
        endDateTextView = findViewById(R.id.book_end_date);
        timeSpentTextView = findViewById(R.id.book_time_spent);

        bookCover = findViewById(R.id.book_img_cover);

        Bundle extras = getIntent().getExtras();

        if(getIntent().hasExtra(EXTRA_BOOK_TITLE)) {
            titleTextView.setText(getIntent().getStringExtra(EXTRA_BOOK_TITLE));
        }
        if(getIntent().hasExtra(EXTRA_BOOK_AUTHOR)) {
            authorTextView.setText(getIntent().getStringExtra(EXTRA_BOOK_AUTHOR));
        }
        if(getIntent().hasExtra(EXTRA_BOOK_PAGE_COUNT)) {
//            pageCountTextView.setText(getIntent().getStringExtra(EXTRA_BOOK_PAGE_COUNT));
            pageCountTextView.setText(String.valueOf(getIntent().getIntExtra(EXTRA_BOOK_PAGE_COUNT, -1)));
        }
        if(getIntent().hasExtra(EXTRA_BOOK_START_DATE)) {
            Date d = new Date();
            d.setTime(getIntent().getLongExtra(EXTRA_BOOK_START_DATE, -1));
            startDateTextView.setText(d.toString());
        }
        if(getIntent().hasExtra(EXTRA_BOOK_END_DATE)) {
            Date d = new Date();
            d.setTime(getIntent().getLongExtra(EXTRA_BOOK_END_DATE, -1));
            endDateTextView.setText(d.toString());
        }
        if(getIntent().hasExtra(EXTRA_BOOK_TIME_SPENT)) {
            timeSpentTextView.setText(String.valueOf(getIntent().getIntExtra(EXTRA_BOOK_TIME_SPENT, 0)));
        }

        if (getIntent().hasExtra(EXTRA_BOOK_COVER)) {
            Picasso.with(getApplicationContext())
                    .load(IMAGE_URL_BASE + getIntent().getStringExtra(EXTRA_BOOK_COVER) + "-L.jpg")
                    .placeholder(R.drawable.ic_book_black_24dp).into(bookCover);
        } else {
            bookCover.setImageResource(R.drawable.ic_book_black_24dp);
        }

//        bookTitleTextView.setText(bookSearch.getTitle());
//        bookAuthorTextView.setText(TextUtils.join(", ", bookSearch.getAuthors()));
//        bookPageCountTextView.setText(bookSearch.getPageCount());
//        bookStartDateTextView.setText(startDate.toString());

        bookViewModel = ViewModelProviders.of(this).get(BookViewModel.class);

        final Button button = findViewById(R.id.button_save);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent replyIntent = new Intent();
//                if(TextUtils.isEmpty(editTitleEditText.getText())
//                        || TextUtils.isEmpty(editTitleEditText.getText())) {
//                    setResult(RESULT_CANCELED, replyIntent);
//                } else {
//                    String title = editTitleEditText.getText().toString();
//                    replyIntent.putExtra(EXTRA_BOOK_TITLE, title);
//                    String author = editAuthorEditText.getText().toString();
//                    replyIntent.putExtra(EXTRA_BOOK_AUTHOR, author);
//                    setResult(RESULT_OK, replyIntent);
//                }
                finish();
            }
        });
    }
}