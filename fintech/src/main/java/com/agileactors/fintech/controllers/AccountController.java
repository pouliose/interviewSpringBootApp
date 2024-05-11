package com.agileactors.fintech.controllers;

import com.agileactors.fintech.domain.entities.Account;
import com.agileactors.fintech.services.AccountService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("api/accounts")
@AllArgsConstructor
public class AccountController {
    private AccountService accountService;

    @PostMapping(path="")
    public ResponseEntity<Account> createAccount(@RequestBody Account account) {
        accountService.createAccount(account);
        return new ResponseEntity<>(account, HttpStatus.CREATED);
    }

    @GetMapping(path="/{accountId}")
    public ResponseEntity<Account> getAccount(@PathVariable("accountId") String accountId) {
        Optional<Account> account = accountService.getAccount(accountId);

        return account.map(value -> new ResponseEntity<>(value, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

}
