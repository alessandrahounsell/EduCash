package com.example.educash;

public class Transaction {
    private int transactionId;
    private int categoryId;
    private String categoryName;
    private boolean take;
    private double transactionAmount;
    private String transactionDate;

    public Transaction(int transactionId, int categoryId, String categoryName, boolean take, double transactionAmount, String transactionDate) {
        this.transactionId = transactionId;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.take = take;
        this.transactionAmount = transactionAmount;
        this.transactionDate = transactionDate;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public boolean isTake() {
        return take;
    }

    public void setTake(boolean take) {
        this.take = take;
    }

    public double getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(double transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionId=" + transactionId +
                ", categoryId=" + categoryId +
                ", categoryName='" + categoryName + '\'' +
                ", take=" + take +
                ", transactionAmount=" + transactionAmount +
                ", transactionDate='" + transactionDate + '\'' +
                '}';
    }
}
