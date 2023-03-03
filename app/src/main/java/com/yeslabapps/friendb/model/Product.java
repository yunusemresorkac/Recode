package com.yeslabapps.friendb.model;

public class Product {

    private String productId;
    private String title;
    private String description;
    private int stock;
    private String image;
    private int gold;


    public Product(){

    }

    public Product(String productId, String title, String description, int stock, String image, int gold) {
        this.productId = productId;
        this.title = title;
        this.description = description;
        this.stock = stock;
        this.image = image;
        this.gold = gold;
    }

    public String getProductId() {
        return productId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getStock() {
        return stock;
    }

    public String getImage() {
        return image;
    }

    public int getGold() {
        return gold;
    }
}
