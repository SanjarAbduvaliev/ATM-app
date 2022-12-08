package com.example.atmapp.repository;

import com.example.atmapp.entity.Address;
import com.example.atmapp.entity.OutputInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutputRepository extends JpaRepository<OutputInfo,Integer> {
}
