package com.example.booktracker.booksearch;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class BookSearch implements Serializable {

    @SerializedName("title")
    private String title;
    @SerializedName("author_name")
    private List<String> authors;
    @SerializedName("cover_i")
    private String cover;

    public BookSearch(String title, List<String> authors, String cover) {
        this.title = title;
        this.authors = authors;
        this.cover = cover;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }
}
