package com.cmloopy.quizzi.models;

public class FoodItem {
    private String id;
    private String name;
    private String type; // "Bánh", "Đồ uống", "Café"
    private double price;
    private int quantity;
    private float rating;

    public FoodItem() {
        // Default constructor
    }

    public FoodItem(String id, String name, String type, double price, int quantity, float rating) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.price = price;
        this.quantity = quantity;
        this.rating = rating;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    // Get the prefix for ID based on type
    public static String getIdPrefix(String type) {
        switch (type) {
            case "Bánh":
                return "B";
            case "Đồ uống":
                return "U";
            case "Café":
                return "F";
            default:
                return "";
        }
    }

    // Method to get total price
    public double getTotalPrice() {
        return price * quantity;
    }
}