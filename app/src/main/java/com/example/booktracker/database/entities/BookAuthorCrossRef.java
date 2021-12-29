package com.example.booktracker.database.entities;

import androidx.room.Entity;

@Entity(primaryKeys = {"bookId", "authorId"})
public class BookAuthorCrossRef {
    public long bookId;
    public long authorId;

    public BookAuthorCrossRef(long bookId, long authorId) {
        this.bookId = bookId;
        this.authorId = authorId;
    }
}
