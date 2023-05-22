package com.example.mysamsungapp.ui.home;

public class ItemOperation {
    int id;
    int image;
    String category;
    String description;
    String date;
    int amount;

    public ItemOperation(int id, int image, String category, String description, String date, int amount) {
        this.id = id;
        this.image = image;
        this.category = category;
        this.description = description;
        this.date = date;
        this.amount = amount;
    }
}
