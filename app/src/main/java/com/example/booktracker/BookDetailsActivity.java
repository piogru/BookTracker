package com.example.booktracker;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.OpenableColumns;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.example.booktracker.MainActivity.IMAGE_URL_BASE;

public class BookDetailsActivity extends AppCompatActivity {

    final static int REQUEST_CODE_READ_EXTERNAL_STORAGE = 0x1234;

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
    private TextView fileNameTextView;
    private ImageView bookCover;

    private Button readButton;
    private Button selectFileButton;
    private Button editFileButton;

    private Date startReading;
    private Date endReading;

    private BookViewModel bookViewModel;
    private BookWithAuthors book;

    private String action;

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
        fileNameTextView = findViewById(R.id.book_file_name);

        bookCover = findViewById(R.id.book_img_cover);
        readButton = findViewById(R.id.button_read);
        selectFileButton = findViewById(R.id.button_select_file);
        editFileButton = findViewById(R.id.button_edit_file);

        bookViewModel = ViewModelProviders.of(this).get(BookViewModel.class);

        action = null;

        bookViewModel.findBookWithTitle(getIntent().getStringExtra(EXTRA_BOOK_TITLE)).observe(this, new Observer<BookWithAuthors>() {
            @Override
            public void onChanged(BookWithAuthors select) {
                book = select;

                titleTextView.setText(book.book.getTitle());
                authorTextView.setText(TextUtils.join(", ", book.authors));
                pageCountTextView.setText(String.valueOf(book.book.getPageCount()));

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                Date d = book.book.getStartDate();

//                startDateTextView.setText(d.toString());
                startDateTextView.setText(new SimpleDateFormat().format(d));

                timeSpentTextView.setText(String.valueOf(book.book.getTimeSpent()));

                if (book.book.getCover() != null) {
                    Picasso.with(getApplicationContext())
                            .load(IMAGE_URL_BASE + book.book.getCover() + "-L.jpg")
                            .placeholder(R.drawable.ic_book_black_24dp).into(bookCover);
                }
                else {
                    bookCover.setImageResource(R.drawable.ic_book_black_24dp);
                }

                if(book.book.getEndDate() != null) {
                    d = book.book.getEndDate();
                    findViewById(R.id.book_end_date_layout).setVisibility(View.VISIBLE);
//                    endDateTextView.setText(d.toString());
                    endDateTextView.setText(new SimpleDateFormat().format(d));

                    // confirmation dialog
                    final Button button = findViewById(R.id.button_save);
                    button.setVisibility(View.VISIBLE);

                    FinishConfirmationDialogFragment confirmationDialog = new FinishConfirmationDialogFragment();

                    button.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View view) {
                            confirmationDialog.show(getSupportFragmentManager(),
                                    FinishConfirmationDialogFragment.TAG);
                        }
                    });

                    if(book.book.getFileUri() != null) {
                        selectFileButton.setVisibility(View.GONE);
                        readButton.setVisibility(View.VISIBLE);
                        editFileButton.setVisibility(View.GONE);
                    } else {
                        selectFileButton.setVisibility(View.GONE);
                        readButton.setVisibility(View.GONE);
                        editFileButton.setVisibility(View.GONE);
                    }
                }
                else {
                    selectFileButton.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View view) {
                            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                action = "select";
                                requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_READ_EXTERNAL_STORAGE);
                            } else {
                                openFile();
                            }
                        }
                    });

                    editFileButton.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View view) {
                            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                action = "select";
                                requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_READ_EXTERNAL_STORAGE);
                            } else {
                                openFile();
                            }
                        }
                    });

                    if(book.book.getFileUri() != null) {
                        selectFileButton.setVisibility(View.GONE);
                        readButton.setVisibility(View.VISIBLE);
                        editFileButton.setVisibility(View.VISIBLE);
                    } else {
                        selectFileButton.setVisibility(View.VISIBLE);
                        readButton.setVisibility(View.GONE);
                        editFileButton.setVisibility(View.GONE);
                    }
                }

                if(book.book.getFileUri() != null) {
                    Cursor returnCursor =
                            getContentResolver().query(Uri.parse(book.book.getFileUri()), null, null, null, null);

                    int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                    returnCursor.moveToFirst();

                    fileNameTextView.setText(returnCursor.getString(nameIndex));

                    readButton.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View view) {
                            startReading = new Date();

                            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                action = "read";
                                requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_READ_EXTERNAL_STORAGE);
                            } else {
                                beginReading();
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_READ_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && action != null) {
                if(action == "select") {
                    openFile();
                } else {
                    beginReading();
                }

            }
        }
    }

    private void beginReading() {
        Intent intent = new Intent(BookDetailsActivity.this, BookReaderActivity.class);
        intent.putExtra(BookReaderActivity.EXTRA_FILE_URI, book.book.getFileUri());
        activityResultLaunch.launch(intent);
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