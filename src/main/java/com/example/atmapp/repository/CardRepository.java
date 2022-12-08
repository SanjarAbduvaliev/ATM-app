package com.example.atmapp.repository;

import com.example.atmapp.entity.Card;
import com.example.atmapp.entity.enums.CardName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CardRepository extends JpaRepository<Card,Integer> {

    Optional<Card> findByCardNumbeerAndSVVcode(String cardNumbeer, String SVVcode);
    Optional<Card> findByCardNumbeer(String cardNumbeer);

}
