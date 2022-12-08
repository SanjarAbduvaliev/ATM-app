package com.example.atmapp.controller;

import com.example.atmapp.payload.UserTransferDto;
import com.example.atmapp.service.TransferAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/actionGetMoney")
public class ActionController{

    @Autowired
    TransferAction transferAction;

    @PreAuthorize(value = "hasAnyRole('USER')")
    @PostMapping("/uzCard/{atmID}")
    public ResponseEntity<?>  actionUzcard(@PathVariable Integer atmID, @RequestBody UserTransferDto userTransferDto){
        return ResponseEntity.ok(transferAction.transferUZCARD(atmID,userTransferDto));
    }
    @PostMapping("/VisaCard/{atmID}")
    public ResponseEntity<?>  actionVISA(@PathVariable Integer atmID, @RequestBody UserTransferDto userTransferDto){
        return ResponseEntity.ok(transferAction.transferVisa(atmID,userTransferDto));
    }


}
