package com.example.booktracker.database.entities;

import androidx.room.Entity;

@Entity(primaryKeys = {"bookId", "authorId"})
public class BookAuthorCrossRef {
    public long bookId;
    public long authorId;
}
