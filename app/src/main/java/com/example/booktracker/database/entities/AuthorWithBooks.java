package com.example.booktracker.database.entities;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.List;

public class AuthorWithBooks {
    @Embedded
    public Author author;
    @Relation(
            parentColumn = "authorId",
            entityColumn = "bookId",
            associateBy = @Junction(BookAuthorCrossRef.class)
    )
    public List<Book> books;

}