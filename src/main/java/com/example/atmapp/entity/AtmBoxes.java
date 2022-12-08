package com.example.atmapp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class AtmBoxes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)

    private Integer sum1000;
    private Integer sum5000;
    private Integer sum10000;
    private Integer sum50000;
    private Integer sum100000;


    private Integer USD1;
    private Integer USD10;
    private Integer USD5;
    private Integer USD20;
    private Integer USD50;
    private Integer USD100;

    @ManyToOne
    private ATM atm;


}
