package com.agileactors.fintech.services.impl;

import com.agileactors.fintech.domain.entities.Account;
import com.agileactors.fintech.domain.entities.Transaction;
import com.agileactors.fintech.repositories.AccountRepository;
import com.agileactors.fintech.repositories.TransactionRepository;
import com.agileactors.fintech.services.AccountService;
import com.agileactors.fintech.services.TransactionService;
import com.agileactors.fintech.validations.TransactionValidations;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;


@AllArgsConstructor
@Service
public class TransactionServiceImpl implements TransactionService {

    private final AccountRepository accountRepository;

    private final TransactionRepository transactionRepository;

    private final AccountService accountService;

    @Override
    public Transaction createTransaction(Transaction transaction) {

        TransactionValidations transactionValidations = new TransactionValidations(accountService);

        transactionValidations.validateTransaction(transaction);

        updateSourceAccountBalance(transaction, accountService, accountRepository);

        updateTargetAmount(transaction, accountService, accountRepository);

        transaction.setExecutedAt(LocalDateTime.now());

        Optional<Account> sourceAccount = accountRepository.findById(transaction.getSourceAccountId());
        sourceAccount.map(a -> a.getTransactions().add(transaction));

        Optional<Account> targetAccount = accountRepository.findById(transaction.getTargetAccountId());
        targetAccount.map(a -> a.getTransactions().add(transaction));

        Transaction transactionSaved = transactionRepository.save(transaction);
        Optional<Account> sourceAccount2 = accountRepository.findById(transaction.getSourceAccountId());
        if(sourceAccount2.isPresent()){
            sourceAccount2.get().getTransactions().add(transaction);
            accountService.update(sourceAccount2.get().getId(),transaction);
        }
        return transactionSaved;
    }

    private static void updateSourceAccountBalance(Transaction transaction, AccountService accountService, AccountRepository accountRepository) {

        Account sourceAccount = accountService.getAccount(transaction.getSourceAccountId()).get();
        sourceAccount.setBalance(sourceAccount.getBalance().subtract(transaction.getAmount()));
        accountRepository.save(sourceAccount);
    }

    private void updateTargetAmount(Transaction transaction, AccountService accountService, AccountRepository accountRepository) {

        Account targetAccount = accountService.getAccount(transaction.getTargetAccountId()).get();
        targetAccount.setBalance(targetAccount.getBalance().add(transaction.getAmount()));
        accountRepository.save(targetAccount);
    }

    @Override
    public Optional<Transaction> getTransaction(String id) {

        return transactionRepository.findById(id);
    }
}
