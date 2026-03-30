package com.example.demo.model;


public class PaymentRequest {

    private String orderId;
    private double amount;

    public PaymentRequest() {}

    public PaymentRequest(String orderId, double amount) {
        this.orderId = orderId;
        this.amount = amount;
    }

    public String getOrderId() {
        return orderId;
    }

    public double getAmount() {
        return amount;
    }
}