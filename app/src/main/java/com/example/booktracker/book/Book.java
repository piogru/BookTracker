package com.example.booktracker.book;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity(tableName = "book")
public class Book {

    @PrimaryKey(autoGenerate = true)
    private Long id;
    private String title;
    private String author;
    private int pageCount;
    private Date startDate;
    private String cover;

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

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public Book(String title, String author, int pageCount, Date startDate, String cover) {
        this.title = title;
        this.author = author;
        this.pageCount = pageCount;
        this.startDate = startDate;
        this.cover = cover;
    }

}
