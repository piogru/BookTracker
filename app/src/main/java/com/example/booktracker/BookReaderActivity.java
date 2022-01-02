package com.example.booktracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Context;
import android.content.Intent;
//import android.net.Uri;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;

import java.io.File;

public class BookReaderActivity extends AppCompatActivity {

    public final static String EXTRA_FILE_URI = "EXTRA_FILE_URI";

    private PDFView pdfView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_reader);

        pdfView = findViewById(R.id.pdf_view);

        Intent intent = getIntent();
        String fileUri = intent.getStringExtra(EXTRA_FILE_URI);

        Uri uri = Uri.parse(fileUri);
        File file = new File(uri.getPath());

        pdfView.fromUri(uri).defaultPage(1).onLoad(new OnLoadCompleteListener() {
            @Override
            public void loadComplete(int nbPages) {
                Toast.makeText(BookReaderActivity.this, String.valueOf(nbPages), Toast.LENGTH_LONG).show();
            }
        }).load();
    }

    @Override
    public void finish() {
        Intent replyIntent = new Intent();
        setResult(300, replyIntent);

        super.finish();
    }
}