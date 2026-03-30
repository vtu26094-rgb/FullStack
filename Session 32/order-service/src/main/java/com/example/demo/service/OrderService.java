package com.example.demo.service;


import com.example.demo.model.PaymentRequest;
import com.example.demo.model.PaymentResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Service
public class OrderService {

    public String createOrder(double amount) {

        String orderId = UUID.randomUUID().toString();

        RestTemplate restTemplate = new RestTemplate();

        PaymentRequest paymentRequest =
                new PaymentRequest(orderId, amount);

        PaymentResponse paymentResponse =
                restTemplate.postForObject(
                        "http://localhost:8082/payments",
                        paymentRequest,
                        PaymentResponse.class
                );

        if (paymentResponse != null &&
                paymentResponse.getStatus().equals("SUCCESS")) {

            return "Order Confirmed. Payment ID: "
                    + paymentResponse.getPaymentId();
        }

        return "Payment Failed. Order Cancelled";
    }
}