package com.example.atmapp.payload;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserTransferDto {

    @Pattern(regexp = "^[0-9]{4}$")
    private String pinCode;
    private Double summa;
    @Pattern(regexp = "^[0-9]{16}$")
    private String cardNumbeer;
    private Integer bankId;
    private String SVVcode;
    private String cardType;
}
