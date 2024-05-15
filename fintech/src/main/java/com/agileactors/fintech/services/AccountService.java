package com.agileactors.fintech.services;

import com.agileactors.fintech.domain.entities.Account;
import com.agileactors.fintech.domain.entities.Transaction;

import java.util.Optional;

public interface AccountService {
    Account createAccount(Account account);

    Optional<Account> getAccount(String id);

    Account update(String id, Transaction transaction);
}
