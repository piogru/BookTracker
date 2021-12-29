package com.example.booktracker.database;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.booktracker.database.entities.Author;
import com.example.booktracker.database.entities.Book;

import java.util.List;

public class AuthorViewModel extends AndroidViewModel {
    private AuthorRepository authorRepository;
    private LiveData<List<Author>> authors;

    public AuthorViewModel(@NonNull Application application) {
        super(application);
        authorRepository = new AuthorRepository(application);
        authors = authorRepository.findAllAuthors();
    }

    public LiveData<List<Author>> findAll() {
        return authors;
    }

    public void insert(Author author) {
        authorRepository.insert(author);
    }

    public void update(Author author) {
        authorRepository.update(author);
    }

    public void delete(Author author) {
        authorRepository.delete(author);
    }

    public LiveData<Author> getLastAuthorLive(){
        return authorRepository.getLastAuthorLive();
    }
}
