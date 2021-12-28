package com.example.booktracker.booksearch;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class BookSearch implements Serializable {

    @SerializedName("title")
    private String title;
    @SerializedName("author_name")
    private List<String> authors;
    @SerializedName("number_of_pages_median")
    private String pageCount;
    @SerializedName("cover_i")
    private String cover;

    public BookSearch(String title, List<String> authors, String pageCount, String cover) {
        this.title = title;
        this.authors = authors;
        this.pageCount = pageCount;
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

    public String getPageCount() {
        return pageCount;
    }

    public void setPageCount(String pageCount) {
        this.pageCount = pageCount;
    }
}
