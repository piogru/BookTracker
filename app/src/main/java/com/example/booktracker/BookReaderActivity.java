package com.example.booktracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.FileProvider;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
//import android.net.Uri;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.OpenableColumns;
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

//        pdfView.getCurrentPage();

        pdfView.fromUri(uri).defaultPage(1).onLoad(new OnLoadCompleteListener() {
            @Override
            public void loadComplete(int nbPages) {
                Toast.makeText(BookReaderActivity.this, String.valueOf(nbPages), Toast.LENGTH_LONG).show();
            }
        }).load();

        createNotificationChannel();

        Resources resources = getResources();

        Intent notificationIntent = new Intent(this, BookReaderActivity.class);
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        notificationIntent.putExtra(EXTRA_FILE_URI, intent.getStringExtra(EXTRA_FILE_URI));
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "READER_CHANNEL")
                .setSmallIcon(R.drawable.ic_book_black_24dp)
                .setContentTitle(resources.getString(R.string.app_name))
                .setContentText(resources.getString(R.string.notification_text) + " " +
                        queryName(getContentResolver(), uri))
                .setOngoing(true)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(0, builder.build());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.cancel(0);
    }

    private String queryName(ContentResolver resolver, Uri uri) {
        Cursor returnCursor =
                resolver.query(uri, null, null, null, null);
        assert returnCursor != null;
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        returnCursor.moveToFirst();
        String name = returnCursor.getString(nameIndex);
        returnCursor.close();
        return name;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("READER_CHANNEL", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    @Override
    public void finish() {
        Intent replyIntent = new Intent();
        setResult(300, replyIntent);

        super.finish();
    }
}