package com.example.atmapp.repository;

import com.example.atmapp.entity.ATM;
import com.example.atmapp.entity.Bank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AtmRepository extends JpaRepository<ATM,Integer> {
    Optional<ATM> findById(Integer id);

}
