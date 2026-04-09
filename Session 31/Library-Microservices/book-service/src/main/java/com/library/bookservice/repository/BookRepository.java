package com.library.bookservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.library.bookservice.entity.Book;

public interface BookRepository extends JpaRepository<Book, Long> {

}