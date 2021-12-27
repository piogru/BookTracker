package com.example.booktracker.booksearch;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BookContainer {
    @SerializedName("docs")
    private List<BookSearch> bookList;

    public List<BookSearch> getBookList() {
        return bookList;
    }

    public void setBookList(List<BookSearch> bookList) {
        this.bookList = bookList;
    }
}
