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

    public final static String EXTRA_FILE_NAME = "EXTRA_FILE_NAME";

    private PDFView pdfView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_reader);

        pdfView = findViewById(R.id.pdf_view);

        Intent intent = getIntent();
        String fileName = intent.getStringExtra(EXTRA_FILE_NAME);

        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) +
                "/" + fileName);
        Context context = getBaseContext();
        Uri pdfURI = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);

        //SACRIFICE MEMORY FOR QUALITY
        //pdfView.useBestQuality(true)

        File downloadsFolder= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        File[] files=downloadsFolder.listFiles();

        if(file.canRead())
        {
            //LOAD IT
            pdfView.fromFile(file).defaultPage(1).onLoad(new OnLoadCompleteListener() {
                @Override
                public void loadComplete(int nbPages) {
                    Toast.makeText(BookReaderActivity.this, String.valueOf(nbPages), Toast.LENGTH_LONG).show();
                }
            }).load();


        }
    }

//    @Override
//    protected void onDestroy() {
//        // call the superclass method first
//        super.onDestroy();
//
//        Intent replyIntent = new Intent();
//
//        // 300 - finish reading
//        setResult(300, replyIntent);
//        finish();
//    }

    @Override
    public void finish() {
        Intent replyIntent = new Intent();
        setResult(300, replyIntent);

        super.finish();
    }
}