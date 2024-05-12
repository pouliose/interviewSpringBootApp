package com.agileactors.fintech.controllers;

import com.agileactors.fintech.domain.entities.Transaction;
import com.agileactors.fintech.services.TransactionService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("api/transfer")
@AllArgsConstructor
public class TransactionController {
    private TransactionService transactionService;

    @PostMapping(path="")
    public ResponseEntity<Transaction> createTransaction(@RequestBody Transaction transaction) {
        transactionService.createTransaction(transaction);
        return new ResponseEntity<>(transaction, HttpStatus.CREATED);
    }

    @GetMapping(path="/{transactionId}")
    public ResponseEntity<Transaction> getTransaction(@PathVariable("transactionId") String transactionId) {
        Optional<Transaction> transaction = transactionService.getTransaction(transactionId);

        return transaction.map(value -> new ResponseEntity<>(value, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

}
