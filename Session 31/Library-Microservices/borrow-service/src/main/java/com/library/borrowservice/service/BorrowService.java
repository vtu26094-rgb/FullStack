package com.library.borrowservice.service;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.library.borrowservice.entity.Borrow;
import com.library.borrowservice.repository.BorrowRepository;

@Service
public class BorrowService {

    @Autowired
    private BorrowRepository repository;

    @Autowired
    private RestTemplate restTemplate;

    public Borrow borrowBook(Borrow borrow) {

        // Check if user exists
        restTemplate.getForObject(
                "http://localhost:8081/users/" + borrow.getUserId(),
                Object.class);

        // Check if book exists
        restTemplate.getForObject(
                "http://localhost:8082/books",
                Object.class);

        borrow.setBorrowDate(LocalDate.now());

        return repository.save(borrow);
    }
}