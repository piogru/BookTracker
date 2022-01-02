package com.example.booktracker;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProviders;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
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

    private Button readButton;

    private Date startReading;
    private Date endReading;

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
        readButton = findViewById(R.id.button_read);

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
            endDateTextView.setVisibility(View.VISIBLE);
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

        // confirmation dialog
        if(!getIntent().hasExtra(EXTRA_BOOK_END_DATE)) {
            final Button button = findViewById(R.id.button_save);
            button.setVisibility(View.VISIBLE);

            FinishConfirmationDialogFragment confirmationDialog = new FinishConfirmationDialogFragment();

            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    confirmationDialog.show(getSupportFragmentManager(),
                            FinishConfirmationDialogFragment.TAG);
                }
            });

            readButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    startReading = new Date();

                    Intent intent = new Intent(BookDetailsActivity.this, BookReaderActivity.class);
                    intent.putExtra(BookReaderActivity.EXTRA_FILE_NAME, "boska-komedia.pdf");

                    activityResultLaunch.launch(intent);
//                    startActivity(intent);
                }
            });
        }
    }

    ActivityResultLauncher<Intent> activityResultLaunch = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == 300) {
                        endReading = new Date();
                        Long timeDiff = (endReading.getTime() - startReading.getTime()) / 1000 / 60;

                        Toast.makeText(BookDetailsActivity.this, "Time reading: " + String.valueOf(timeDiff) + " minutes", Toast.LENGTH_LONG).show();
                    }
                }
            });
}