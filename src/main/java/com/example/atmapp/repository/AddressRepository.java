package com.example.atmapp.repository;

import com.example.atmapp.entity.Address;
import com.example.atmapp.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address,Integer> {
}
