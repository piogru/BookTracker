package com.example.booktracker.database.entities;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.List;

public class BookWithAuthors {
    @Embedded
    public Book book;
    @Relation(
            parentColumn = "bookId",
            entityColumn = "authorId",
            associateBy = @Junction(BookAuthorCrossRef.class)
    )
    public List<Author> authors;

}
