package com.agileactors.fintech.services;

import com.agileactors.fintech.domain.entities.Transaction;

import java.util.Optional;

public interface TransactionService {
    Transaction createTransaction(Transaction Transaction);

    Optional<Transaction> getTransaction(String id);
}
