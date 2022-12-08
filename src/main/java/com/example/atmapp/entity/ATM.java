package com.example.atmapp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ATM {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Bank bank;


    @OneToMany(mappedBy = "atm",cascade = CascadeType.ALL)
    private Set<AtmBoxes> atmBoxes;

    @OneToOne(cascade = CascadeType.ALL)
    private Address addressAtm;

    @OneToMany
    private List<OutputInfo > outputInfo;
}
