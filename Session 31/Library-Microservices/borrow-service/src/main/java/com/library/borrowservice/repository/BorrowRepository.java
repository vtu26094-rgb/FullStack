package com.library.borrowservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.library.borrowservice.entity.Borrow;

public interface BorrowRepository extends JpaRepository<Borrow, Long> {

}