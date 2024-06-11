package com.javaguides.springboot.skills.model;
import java.util.List;


public class Book {
    private int id;
    private String title;
    private String author;
    private int year;
    private int price;
    private List<Genre> genres;

    public Book(int id, String title, String author, int year, int price, List<Genre> genres) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.year = year;
        this.price = price;
        this.genres = genres;
    }

    public int getId() {
        return id;
    }
    public int getPrice() {
        return price;
    }
    public int getYear() {
        return year;
    }
    public String getAuthor() {
        return author;
    }
    public String getTitle() {
        return title;
    }
    public void setAuthor(String author) {
        this.author = author;
    }
    public List<Genre> getGenres() {
        return genres;
    }
    public void setGenre(List<Genre> genres) {
        this.genres = genres;
    }
    public void setId(int id) {
        this.id = id;
    }
    public void setPrice(int price) {
        this.price = price;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setYear(int year) {
        this.year = year;
    }
}
