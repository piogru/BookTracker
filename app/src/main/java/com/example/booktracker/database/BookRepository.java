package com.example.booktracker.database;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.booktracker.database.entities.Book;

import java.util.List;

public class BookRepository {
    private BookDao bookDao;
    private LiveData<List<Book>> books;

    BookRepository(Application application) {
        BookDatabase database = BookDatabase.getDatabase(application);
        bookDao = database.bookDao();
        books = bookDao.findAllBooks();
    }

    LiveData<List<Book>> findAllBooks() {
        return books;
    }

    void insert(Book book) {
        BookDatabase.databaseWriteExecutor.execute(() -> {
            bookDao.insertBook(book);
        });
    }

    void update(Book book) {
        BookDatabase.databaseWriteExecutor.execute(() -> {
            bookDao.updateBook(book);
        });
    }

    void delete(Book book) {
        BookDatabase.databaseWriteExecutor.execute(() -> {
            bookDao.deleteBook(book);
        });
    }
}
