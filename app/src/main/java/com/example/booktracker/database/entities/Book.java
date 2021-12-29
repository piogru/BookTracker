package com.example.booktracker.database.entities;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "book",
        indices = {@Index(value = {"title"}, unique = true)})
public class Book {

    @PrimaryKey(autoGenerate = true)
    private Long bookId;
    private String title;
    private int pageCount;
    private Date startDate;
    private String cover;

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public Book(String title, int pageCount, Date startDate, String cover) {
        this.title = title;
        this.pageCount = pageCount;
        this.startDate = startDate;
        this.cover = cover;
    }

}
