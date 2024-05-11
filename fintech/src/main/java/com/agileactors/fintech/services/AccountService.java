package com.agileactors.fintech.services;

import com.agileactors.fintech.domain.entities.Account;

import java.util.Optional;

public interface AccountService {
    Account createAccount(Account account);

    Optional<Account> getAccount(String id);
}
