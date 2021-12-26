package com.example.booktracker.book;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.UUID;

@Entity(tableName = "book")
public class Book {

    @PrimaryKey(autoGenerate = true)
    private Long id;
    private String title;
    private String author;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Book() {
    }

    public Book(String title, String author) {
        this.title = title;
        this.author = author;
    }

//    @SerializedName("title")
//    private String title;
//    @SerializedName("author_name")
//    private List<String> authors;
//    @SerializedName("cover_i")
//    private String cover;
//
//    public Book(String title, List<String> authors, String cover) {
//        this.title = title;
//        this.authors = authors;
//        this.cover = cover;
//    }
//
//    public String getTitle() {
//        return title;
//    }
//
//    public void setTitle(String title) {
//        this.title = title;
//    }
//
//    public List<String> getAuthors() {
//        return authors;
//    }
//
//    public void setAuthors(List<String> authors) {
//        this.authors = authors;
//    }
//
//    public String getCover() {
//        return cover;
//    }
//
//    public void setCover(String cover) {
//        this.cover = cover;
//    }
}
