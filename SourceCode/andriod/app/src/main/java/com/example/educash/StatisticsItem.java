package com.example.educash;

public class StatisticsItem {
    private String catName;
    private double catTotal;
    private double percentage;

    // Constructor
    public StatisticsItem(String catName, double catTotal, double percentage) {
        this.catName = catName;
        this.catTotal = catTotal;
        this.percentage = percentage;
    }

    // Getters
    public String getCatName() {
        return catName;
    }

    public double getCatTotal() {
        return catTotal;
    }

    public double getPercentage() {
        return percentage;
    }
}
