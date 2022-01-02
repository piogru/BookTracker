package com.example.booktracker;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.lifecycle.Observer;
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

import com.example.booktracker.database.BookViewModel;
import com.example.booktracker.database.entities.BookWithAuthors;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Date;
import java.util.List;

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
    private Button selectFileButton;

    private Date startReading;
    private Date endReading;

    private BookViewModel bookViewModel;
    private BookWithAuthors book;

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
        selectFileButton = findViewById(R.id.button_select_file);

        bookViewModel = ViewModelProviders.of(this).get(BookViewModel.class);

        bookViewModel.findBookWithTitle(getIntent().getStringExtra(EXTRA_BOOK_TITLE)).observe(this, new Observer<BookWithAuthors>() {
            @Override
            public void onChanged(BookWithAuthors select) {
                book = select;

                titleTextView.setText(book.book.getTitle());

                authorTextView.setText(TextUtils.join(", ", book.authors));

                pageCountTextView.setText(String.valueOf(book.book.getPageCount()));

                Date d = book.book.getStartDate();
                startDateTextView.setText(d.toString());

                if(book.book.getEndDate() != null) {
                    d = book.book.getEndDate();
                    endDateTextView.setVisibility(View.VISIBLE);
                    endDateTextView.setText(d.toString());
                }

                timeSpentTextView.setText(String.valueOf(book.book.getTimeSpent()));

                if (book.book.getCover() != null) {
                    Picasso.with(getApplicationContext())
                            .load(IMAGE_URL_BASE + book.book.getCover() + "-L.jpg")
                            .placeholder(R.drawable.ic_book_black_24dp).into(bookCover);
                } else {
                    bookCover.setImageResource(R.drawable.ic_book_black_24dp);
                }
            }
        });

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
//                    intent.putExtra(BookReaderActivity.EXTRA_FILE_NAME, "boska-komedia.pdf");
                    intent.putExtra(BookReaderActivity.EXTRA_FILE_URI, book.book.getFileUri());

                    activityResultLaunch.launch(intent);
                }
            });

            selectFileButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    openFile();
                }
            });
        }
    }

    private void openFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");

        activityResultLaunch.launch(intent);
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

                        book.book.setTimeSpent(book.book.getTimeSpent() + timeDiff.intValue());

                        bookViewModel.update(book.book);
                    }

                    if(result.getResultCode() == RESULT_OK) {
                        Uri uri = result.getData().getData();

                        // get persistable read permission
                        book.book.setFileUri(uri.toString());
                        getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);

                        bookViewModel.update(book.book);
                    }
                }
            });
}