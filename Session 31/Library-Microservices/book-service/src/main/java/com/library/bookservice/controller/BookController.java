package com.library.bookservice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.library.bookservice.entity.Book;
import com.library.bookservice.service.BookService;

@RestController
@RequestMapping("/books")
public class BookController {

    @Autowired
    private BookService service;

    @PostMapping
    public Book addBook(@RequestBody Book book) {
        return service.saveBook(book);
    }

    @GetMapping
    public List<Book> getBooks() {
        return service.getAllBooks();
    }
}