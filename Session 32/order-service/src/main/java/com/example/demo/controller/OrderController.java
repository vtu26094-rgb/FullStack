package com.example.demo.controller;


import com.example.demo.model.OrderRequest;
import com.example.demo.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public String createOrder(@RequestBody OrderRequest request) {

        return orderService.createOrder(request.getAmount());

    }
}