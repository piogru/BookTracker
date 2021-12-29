package com.example.booktracker.database;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.booktracker.database.entities.Book;
import com.example.booktracker.database.entities.BookAuthorCrossRef;

import java.util.List;

public class BookAuthorRepository {
    private BookDao bookDao;
    private LiveData<List<Book>> books;

    public BookAuthorRepository(Application application) {
        BookDatabase database = BookDatabase.getDatabase(application);
        bookDao = database.bookDao();
//        books = bookDao.findAllBooks();
    }

//    LiveData<List<Book>> findAllBooks() {
//        return books;
//    }

    public void insert(BookAuthorCrossRef join) {
        BookDatabase.databaseWriteExecutor.execute(() -> {
            bookDao.insertBookAuthorCrossRef(join);
        });
    }

//    void update(Book book) {
//        BookDatabase.databaseWriteExecutor.execute(() -> {
//            bookDao.updateBook(book);
//        });
//    }
//
//    void delete(Book book) {
//        BookDatabase.databaseWriteExecutor.execute(() -> {
//            bookDao.deleteBook(book);
//        });
//    }
}
