package com.example.booktracker.database;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.booktracker.database.entities.Author;

import java.util.List;

public class AuthorRepository {
    private AuthorDao authorDao;
    private LiveData<List<Author>> authors;

    AuthorRepository(Application application) {
        BookDatabase database = BookDatabase.getDatabase(application);
        authorDao = database.authorDao();
        authors = authorDao.findAllAuthors();
    }

    LiveData<List<Author>> findAllAuthors() {
        return authors;
    }

    void insert(Author author) {
        BookDatabase.databaseWriteExecutor.execute(() -> {
            authorDao.insertAuthor(author);
        });
    }

    void update(Author author) {
        BookDatabase.databaseWriteExecutor.execute(() -> {
            authorDao.updateAuthor(author);
        });
    }

    void delete(Author author) {
        BookDatabase.databaseWriteExecutor.execute(() -> {
            authorDao.deleteAuthor(author);
        });
    }

    public LiveData<Author> getLastAuthorLive(){
        return authorDao.getLastAuthorLive();
    }
}
