package com.example.booktracker.database;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.booktracker.database.entities.Author;
import com.example.booktracker.database.entities.Book;
import com.example.booktracker.database.entities.BookWithAuthors;

import java.util.List;

public class BookViewModel extends AndroidViewModel {
    private BookRepository bookRepository;
    private AuthorRepository authorRepository;
    private BookAuthorRepository bookAuthorRepository;

//    private LiveData<List<Book>> books;
    private LiveData<List<BookWithAuthors>> booksWithAuthors;

    public BookViewModel(@NonNull Application application) {
        super(application);
        bookRepository = new BookRepository(application);
//        books = bookRepository.findAllBooks();
        booksWithAuthors = bookRepository.findAllBooksWithAuthors();
    }

//    public LiveData<List<Book>> findAll() {
//        return books;
//    }
    public LiveData<List<BookWithAuthors>> findAllBooksWithAuthors() {
        return booksWithAuthors;
    }

//    public void insert(BookWithAuthors book) {
//        Long bookID = bookRepository.insert(book.book);
//
//        for(Author author : book.authors) {
//            authorRepository.insert(author);
//        }
//    }

    public void insert(Book book) {
        bookRepository.insert(book);
    }

    public void update(Book book) {
        bookRepository.update(book);
    }

    public void delete(BookWithAuthors book) {
        bookRepository.delete(book.book);
    }
}
