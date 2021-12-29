package com.example.booktracker.database;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.booktracker.database.entities.Author;
import com.example.booktracker.database.entities.Book;
import com.example.booktracker.database.entities.BookWithAuthors;

import java.util.List;

public class BookRepository {
    private BookDao bookDao;
    private LiveData<List<Book>> books;
    private LiveData<List<BookWithAuthors>> booksWithAuthors;

    BookRepository(Application application) {
        BookDatabase database = BookDatabase.getDatabase(application);
        bookDao = database.bookDao();
        books = bookDao.findAllBooks();
        booksWithAuthors = bookDao.findAllBooksWithAuthors();
    }

    LiveData<List<Book>> findAllBooks() {
        return books;
    }
    LiveData<List<BookWithAuthors>> findAllBooksWithAuthors() {
        return booksWithAuthors;
    }

    void insertBookWithAuthors(Book book, List<Author> authors) {
        BookDatabase.databaseWriteExecutor.execute(() -> {
            bookDao.insertBookWithAuthors(book, authors);
        });
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
