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
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.booktracker.database.BookViewModel;
import com.example.booktracker.database.entities.BookWithAuthors;
import com.google.android.material.snackbar.Snackbar;
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
    private ImageView expandedCover;

    private Button editFileButton;
    private Button readFAB;
    private Button finishReadingButton;

    private Date startReading;
    private Date endReading;

    private BookViewModel bookViewModel;
    private BookWithAuthors book;

    private String action;

    private Animator currentAnimator;

    // The system "short" animation time duration, in milliseconds. This
    // duration is ideal for subtle animations or animations that occur
    // very frequently.
    private int shortAnimationDuration;


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
        expandedCover = findViewById(R.id.expanded_image);

        finishReadingButton = findViewById(R.id.button_save);
        readFAB = findViewById(R.id.fab_read);
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
                startDateTextView.setText(new SimpleDateFormat().format(d));
//                timeSpentTextView.setText(String.valueOf(book.book.getTimeSpent()));
                timeSpentTextView.setText(getResources().getQuantityString(R.plurals.time_reading, book.book.getTimeSpent(), book.book.getTimeSpent()));

                if (book.book.getCover() != null) {
                    Picasso.with(getApplicationContext())
                            .load(IMAGE_URL_BASE + book.book.getCover() + "-L.jpg")
                            .placeholder(R.drawable.ic_book_black_24dp).into(bookCover);
//                    expandedCover.setImageDrawable(bookCover.getDrawable());
                    Picasso.with(getApplicationContext())
                            .load(IMAGE_URL_BASE + book.book.getCover() + "-L.jpg")
                            .placeholder(R.drawable.ic_book_black_24dp).into(expandedCover);
                }
                else {
                    bookCover.setImageResource(R.drawable.ic_book_black_24dp);
                }


                bookCover.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        zoomImageFromThumb(bookCover, R.id.expanded_image);
                    }
                });

                // Retrieve and cache the system's default "short" animation time.
                shortAnimationDuration = getResources().getInteger(
                        android.R.integer.config_shortAnimTime);


                if(book.book.getEndDate() != null) {
                    d = book.book.getEndDate();
                    findViewById(R.id.book_end_date_layout).setVisibility(View.VISIBLE);
                    endDateTextView.setText(new SimpleDateFormat().format(d));

                    if(book.book.getFileUri() != null) {
                        editFileButton.setVisibility(View.GONE);

                        readFAB.setText(getResources().getString(R.string.button_open_reader));
                        readFAB.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View view) {
                                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                    action = "read";
                                    requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_READ_EXTERNAL_STORAGE);
                                } else {
                                    openFile();
                                }
                            }
                        });
                    } else {
                        editFileButton.setVisibility(View.GONE);

                        readFAB.setText(getResources().getString(R.string.book_select_pdf));
                        readFAB.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View view) {
                                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                    action = "select";
                                    requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_READ_EXTERNAL_STORAGE);
                                } else {
                                    openFile();
                                }
                            }
                        });
                    }
                }
                else {
                    // confirmation dialog
                    finishReadingButton.setVisibility(View.VISIBLE);

                    FinishConfirmationDialogFragment confirmationDialog = new FinishConfirmationDialogFragment();

                    finishReadingButton.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View view) {
                            confirmationDialog.show(getSupportFragmentManager(),
                                    FinishConfirmationDialogFragment.TAG);
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
                        editFileButton.setVisibility(View.VISIBLE);

                        readFAB.setText(getResources().getString(R.string.button_open_reader));
                        readFAB.setOnClickListener(new View.OnClickListener() {
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
                    } else {
                        editFileButton.setVisibility(View.GONE);

                        readFAB.setText(getResources().getString(R.string.book_select_pdf));
                        readFAB.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View view) {
                                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                    action = "select";
                                    requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_READ_EXTERNAL_STORAGE);
                                } else {
                                    openFile();
                                }
                            }
                        });
                    }
                }

                if(book.book.getFileUri() != null) {
                    Cursor returnCursor =
                            getContentResolver().query(Uri.parse(book.book.getFileUri()), null, null, null, null);

                    int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                    returnCursor.moveToFirst();

                    fileNameTextView.setText(returnCursor.getString(nameIndex));

                    readFAB.setContentDescription(getResources().getString(R.string.button_open_reader));
                    readFAB.setText(getResources().getString(R.string.button_open_reader));
                    readFAB.setOnClickListener(new View.OnClickListener() {
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
                    startReading = new Date();
                    beginReading();
                }

            }
        }
    }

    private void beginReading() {
        Intent intent = new Intent(BookDetailsActivity.this, BookReaderActivity.class);
        intent.putExtra(BookReaderActivity.EXTRA_FILE_URI, book.book.getFileUri());
        intent.putExtra(BookReaderActivity.EXTRA_CURRENT_PAGE, book.book.getCurrentPage());
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

                        Snackbar.make(findViewById(R.id.details_coordinator_layout),
                                getResources().getQuantityString(R.plurals.time_reading, timeDiff.intValue(), timeDiff.intValue()),
                                Snackbar.LENGTH_LONG)
                                .setAnchorView(R.id.fab_read)
                                .show();

                        book.book.setTimeSpent(book.book.getTimeSpent() + timeDiff.intValue());
                        book.book.setCurrentPage(result.getData().getIntExtra(BookReaderActivity.EXTRA_CURRENT_PAGE, 0));

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



    private void zoomImageFromThumb(final View thumbView, int imageResId) {
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        if (currentAnimator != null) {
            currentAnimator.cancel();
        }

        // Load the high-resolution "zoomed-in" image.
//        final ImageView expandedImageView = (ImageView) findViewById(
//                R.id.expanded_image);
//        expandedImageView.setImageResource(imageResId);

        // Calculate the starting and ending bounds for the zoomed-in image.
        // This step involves lots of math. Yay, math.
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container
        // view. Also set the container view's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        thumbView.getGlobalVisibleRect(startBounds);
        findViewById(R.id.details_coordinator_layout)
                .getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the "center crop" technique. This prevents undesirable
        // stretching during the animation. Also calculate the start scaling
        // factor (the end scaling factor is always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins, it will position the zoomed-in view in the place of the
        // thumbnail.
        thumbView.setAlpha(0f);
        expandedCover.setVisibility(View.VISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
        expandedCover.setPivotX(0f);
        expandedCover.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(expandedCover, View.X,
                        startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedCover, View.Y,
                        startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedCover, View.SCALE_X,
                        startScale, 1f))
                .with(ObjectAnimator.ofFloat(expandedCover,
                        View.SCALE_Y, startScale, 1f));
        set.setDuration(shortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                currentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                currentAnimator = null;
            }
        });
        readFAB.setVisibility(View.GONE);
        finishReadingButton.setVisibility(View.GONE);
        set.start();
        currentAnimator = set;

        // Upon clicking the zoomed-in image, it should zoom back down
        // to the original bounds and show the thumbnail instead of
        // the expanded image.
        final float startScaleFinal = startScale;
        expandedCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentAnimator != null) {
                    currentAnimator.cancel();
                }

                // Animate the four positioning/sizing properties in parallel,
                // back to their original values.
                AnimatorSet set = new AnimatorSet();
                set.play(ObjectAnimator
                        .ofFloat(expandedCover, View.X, startBounds.left))
                        .with(ObjectAnimator
                                .ofFloat(expandedCover,
                                        View.Y,startBounds.top))
                        .with(ObjectAnimator
                                .ofFloat(expandedCover,
                                        View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator
                                .ofFloat(expandedCover,
                                        View.SCALE_Y, startScaleFinal));
                set.setDuration(shortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedCover.setVisibility(View.GONE);
                        currentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedCover.setVisibility(View.GONE);
                        currentAnimator = null;
                    }
                });
                set.start();
                currentAnimator = set;
                readFAB.setVisibility(View.VISIBLE);
                if(book.book.getEndDate() == null) {
                    finishReadingButton.setVisibility(View.VISIBLE);
                }
            }
        });
    }

}