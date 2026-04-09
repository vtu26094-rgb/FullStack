package com.library.borrowservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.library.borrowservice.entity.Borrow;
import com.library.borrowservice.service.BorrowService;

@RestController
@RequestMapping("/borrow")
public class BorrowController {

    @Autowired
    private BorrowService service;

    @PostMapping
    public Borrow borrowBook(@RequestBody Borrow borrow) {
        return service.borrowBook(borrow);
    }
}