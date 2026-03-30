package com.example.demo.controller;


import com.example.demo.model.PaymentRequest;
import com.example.demo.model.PaymentResponse;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    @PostMapping
    public PaymentResponse processPayment(@RequestBody PaymentRequest request) {

        String paymentId = UUID.randomUUID().toString();
        String status = "SUCCESS";

        return new PaymentResponse(paymentId, status);
    }
}