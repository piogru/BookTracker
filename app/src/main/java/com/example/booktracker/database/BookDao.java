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
import com.example.booktracker.database.entities.BookWithAuthors;

import java.util.List;

@Dao
public interface BookDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    void insertBook(Book book);

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

    @Insert(onConflict = OnConflictStrategy.ABORT)
    void insertAuthor(Author author);

    @Update
    public void updateAuthor(Author author);

    @Delete
    public void deleteAuthor(Author author);

    @Query("DELETE FROM book")
    public void deleteAllAuthors();

    @Query("SELECT * FROM book ORDER BY title")
    public LiveData<List<Book>> findAllAuthors();

//    @Query("SELECT * FROM book WHERE title LIKE :title")
//    public List<Book> findAuthorWithTitle(String title);

    @Transaction
    @Query("SELECT * FROM Book")
    public List<BookWithAuthors> getBooksWithAuthors();

    @Transaction
    @Query("SELECT * FROM Author")
    public List<AuthorWithBooks> getAuthorsWithBooks();

}
