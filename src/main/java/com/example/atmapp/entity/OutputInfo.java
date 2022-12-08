package com.example.atmapp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class OutputInfo {
    @Id
    @GeneratedValue
    private UUID id;
    @Column(nullable = false)
    private Double outputSumma;
    @Column(nullable = false)
    private Date actionDate;
    @ManyToMany
    private List<Card> cards;
}
