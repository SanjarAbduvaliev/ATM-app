package com.example.atmapp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Bank {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Double bankAccountBalance;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "bank",cascade = CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)//qochonki card o'chsa unga  bog'langan tabllari ham o'chib ketadi
    private Set<Card> cards;

    @OneToMany(cascade = CascadeType.ALL)
    private Set<User> user;

    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private Set<ATM> atms;

}
