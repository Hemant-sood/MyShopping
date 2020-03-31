package com.example.myshopping.Models;

public class ProductProperties {
    String  Url, Category, Name,Description, Price,Date,Time;

    public ProductProperties(){}

    public ProductProperties(String url, String category, String name, String description, String price, String date, String time) {
        Url = url;
        Category = category;
        Name = name;
        Description = description;
        Price = price;
        Date = date;
        Time = time;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }
}
