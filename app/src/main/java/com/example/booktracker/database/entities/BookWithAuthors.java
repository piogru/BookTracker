package com.example.booktracker.database.entities;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.List;

public class BookWithAuthors implements Comparable<BookWithAuthors> {
    @Embedded
    public Book book;
    @Relation(
            parentColumn = "bookId",
            entityColumn = "authorId",
            associateBy = @Junction(BookAuthorCrossRef.class)
    )
    public List<Author> authors;

    @Override
    public int compareTo(BookWithAuthors o) {
        return book.compareTo(o.book);
    }
}
