package com.example.atmapp.repository;

import com.example.atmapp.entity.ATM;
import com.example.atmapp.entity.AtmBoxes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AtmBoxesRepository extends JpaRepository<AtmBoxes,Integer> {
}
