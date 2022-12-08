package com.example.atmapp.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collection;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Card implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String cardNumbeer;

    @ManyToOne(optional = false,fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private Bank bank;

    @Column(nullable = false)
    private String SVVcode;


    private Double cardBalance;

    @Column(nullable = false)
    private String pinCode;

    @Column(nullable = false)
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Timestamp expireDate;

    @ManyToOne
    private CardType cardType;

    @ManyToOne(cascade = CascadeType.ALL)
    private User user;

    private boolean  accountNonExpired=true; //bu userni  amal qilish muddati o'tmaganlig

    private boolean accountNonLocked=true; //bu user bloklanmaganligi

    private boolean credentialsNonExpired=true;

    private boolean enabled=false;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getAuthorities();
    }

    @Override
    public String getPassword() {
        return this.pinCode;
    }

    @Override
    public String getUsername() {
        return this.cardNumbeer;
    }

    @Override
    public boolean isAccountNonExpired() {

        Timestamp now = Timestamp.valueOf(LocalDateTime.now());
        if (expireDate.compareTo(now) > 0){
            return this.accountNonExpired=false;
        }
        return this.accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
}


