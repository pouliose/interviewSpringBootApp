package com.agileactors.fintech.services.impl;

import com.agileactors.fintech.domain.entities.Account;
import com.agileactors.fintech.domain.entities.Transaction;
import com.agileactors.fintech.repositories.AccountRepository;
import com.agileactors.fintech.services.AccountService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;


@AllArgsConstructor
@Service
public class AccountServiceImpl implements AccountService {
    private AccountRepository accountRepository;

    @Override
    public Account createAccount(Account account) {
        account.setCreatedAt(LocalDateTime.now());

        return accountRepository.save(account);
    }

    @Override
    public Optional<Account> getAccount(String id) {

        return accountRepository.findById(id);
    }

    @Override
    public Account update(String id, Transaction transaction) {
        Optional<Account> account = accountRepository.findById(id);
        account.get().getTransactions().add(transaction);
        return account.get();
    }
}
