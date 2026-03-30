package com.example.demo.model;


public class PaymentResponse {

    private String paymentId;
    private String status;

    public PaymentResponse(String paymentId, String status) {
        this.paymentId = paymentId;
        this.status = status;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public String getStatus() {
        return status;
    }
}