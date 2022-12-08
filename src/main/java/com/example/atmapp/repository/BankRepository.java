package com.example.atmapp.repository;

import com.example.atmapp.entity.Bank;
import com.example.atmapp.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankRepository extends JpaRepository<Bank,Integer> {
}
