package com.example.booktracker.database;

import android.app.Application;

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
public abstract class BookDao {

    public void insertBookWithAuthors(Book book, List<Author> authors) {
        Long bookId;
        Long authorId;
        BookAuthorCrossRef join;

        bookId = this.insertBook(book);
        if(bookId == -1) {
            return;
        }

        for(Author author : authors){
            Author dbAuthor = this.findAuthorByName(author.getFirstName(), author.getLastName());
            if(dbAuthor != null) {
                authorId = dbAuthor.getAuthorId();
            } else {
                authorId = this.insertAuthor(author);
            }

            join = new BookAuthorCrossRef(bookId, authorId);
            insertBookAuthorCrossRef(join);
        }
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract Long insertBook(Book book);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract Long insertBookAuthorCrossRef(BookAuthorCrossRef join);

    @Update
    abstract public void updateBook(Book book);

    @Delete
    abstract public void deleteBook(Book book);

    @Query("DELETE FROM book")
    abstract public void deleteAllBooks();

    @Query("SELECT * FROM book ORDER BY title")
    abstract public LiveData<List<Book>> findAllBooks();

    @Query("SELECT * FROM book WHERE title LIKE :title")
    abstract public List<Book> findBookWithTitle(String title);

    @Transaction
    @Query("SELECT * FROM Book")
    abstract public LiveData<List<BookWithAuthors>> findAllBooksWithAuthors();



    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract Long insertAuthor(Author author);

    @Update
    abstract public void updateAuthor(Author author);

    @Delete
    abstract public void deleteAuthor(Author author);

    @Query("DELETE FROM book")
    abstract public void deleteAllAuthors();

    @Query("SELECT * FROM author")
    abstract public LiveData<List<Author>> findAllAuthors();

    @Query("SELECT * FROM author WHERE first_name LIKE :firstName AND last_name LIKE :lastName LIMIT 1;")
    abstract public Author findAuthorByName(String firstName, String lastName);

}
