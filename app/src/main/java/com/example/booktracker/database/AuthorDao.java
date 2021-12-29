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
public interface AuthorDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAuthor(Author author);

    @Update
    public void updateAuthor(Author author);

    @Delete
    public void deleteAuthor(Author author);

    @Query("DELETE FROM book")
    public void deleteAllAuthors();

    @Query("SELECT * FROM author")
    public LiveData<List<Author>> findAllAuthors();

//    @Query("SELECT * FROM book WHERE title LIKE :title")
//    public List<Book> findAuthorWithTitle(String title);

    @Transaction
    @Query("SELECT * FROM Author")
    public List<AuthorWithBooks> getAuthorsWithBooks();

    @Query("SELECT * FROM author ORDER BY authorId DESC LIMIT 1")
    LiveData<Author> getLastAuthorLive();

}
