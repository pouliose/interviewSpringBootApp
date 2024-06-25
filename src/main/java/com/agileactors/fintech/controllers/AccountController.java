package com.agileactors.fintech.controllers;

import com.agileactors.fintech.domain.entities.Account;
import com.agileactors.fintech.services.AccountService;
import com.agileactors.fintech.utils.ResponseResult;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("api/accounts")
@AllArgsConstructor
public class AccountController {
    private AccountService accountService;

    @PostMapping(path="")
    public ResponseResult<Account> createAccount(@RequestBody Account account) {
        accountService.createAccount(account);
        return new ResponseResult<>(account, HttpStatus.CREATED, null);
    }

    @GetMapping(path="/{accountId}")
    public ResponseResult<Account> getAccount(@PathVariable("accountId") String accountId) {
        Optional<Account> account = accountService.getAccount(accountId);

        return account.map(value -> new ResponseResult<>(value, HttpStatus.OK, null))
                .orElseGet(() -> new ResponseResult<>(null, HttpStatus.NOT_FOUND, null));
    }

}
