package com.example.booktracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class BookDetailsActivity extends AppCompatActivity {

    public static final String EXTRA_BOOK_TITLE = "com.example.booktracker.book.EDIT_BOOK_TITLE";
    public static final String EXTRA_BOOK_AUTHOR = "com.example.booktracker.book.EDIT_BOOK_AUTHOR";

    private TextView titleTextView;
    private TextView authorTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);

        titleTextView = findViewById(R.id.book_title);
        authorTextView = findViewById(R.id.book_author);
        if(getIntent().hasExtra(EXTRA_BOOK_TITLE)) {
            titleTextView.setText(getIntent().getStringExtra(EXTRA_BOOK_TITLE));
            authorTextView.setText(getIntent().getStringExtra(EXTRA_BOOK_AUTHOR));
        }

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