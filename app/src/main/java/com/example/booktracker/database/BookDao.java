package com.example.booktracker.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.booktracker.database.entities.Author;
import com.example.booktracker.database.entities.AuthorWithBooks;
import com.example.booktracker.database.entities.Book;
import com.example.booktracker.database.entities.BookAuthorCrossRef;
import com.example.booktracker.database.entities.BookWithAuthors;

import java.util.List;

@Dao
public interface BookDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertBook(Book book);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertBookAuthorCrossRef(BookAuthorCrossRef join);

    @Update
    public void updateBook(Book book);

    @Delete
    public void deleteBook(Book book);

    @Query("DELETE FROM book")
    public void deleteAllBooks();

    @Query("SELECT * FROM book ORDER BY title")
    public LiveData<List<Book>> findAllBooks();

    @Query("SELECT * FROM book WHERE title LIKE :title")
    public List<Book> findBookWithTitle(String title);

    @Transaction
    @Query("SELECT * FROM Book")
    public LiveData<List<BookWithAuthors>> findAllBooksWithAuthors();



}
