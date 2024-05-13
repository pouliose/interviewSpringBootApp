package com.agileactors.fintech.controllers;

import com.agileactors.fintech.domain.entities.Transaction;
import com.agileactors.fintech.services.AccountService;
import com.agileactors.fintech.services.TransactionService;
import com.agileactors.fintech.utils.ResponseResult;
import com.agileactors.fintech.validations.TransactionValidations;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("api/transfer")
@AllArgsConstructor
public class TransactionController {
    private TransactionService transactionService;
    private AccountService accountService;

    @PostMapping(path="")
    public ResponseResult<Transaction> createTransaction(@RequestBody Transaction transaction) {

        return checkNewTransactionRequestAndExecute(transaction, accountService);
    }

    private ResponseResult<Transaction> checkNewTransactionRequestAndExecute(Transaction transaction, AccountService accountService) {

        ResponseResult<Transaction> BAD_REQUEST = validateNewTransactionRequest(transaction, accountService);
        if (BAD_REQUEST != null) return BAD_REQUEST;

        transactionService.createTransaction(transaction);

        return new ResponseResult<>(transaction, HttpStatus.CREATED, null);
    }

    private static ResponseResult<Transaction> validateNewTransactionRequest(Transaction transaction, AccountService accountService) {

        TransactionValidations validations = new TransactionValidations(accountService);

        String validationsResult = validations.validateTransaction(transaction);

        if(!validationsResult.isBlank()) return new ResponseResult<>(null, HttpStatus.BAD_REQUEST, validationsResult);
        return null;
    }

    @GetMapping(path="/{transactionId}")
    public ResponseResult<Transaction> getTransaction(@PathVariable("transactionId") String transactionId) {
        Optional<Transaction> transaction = transactionService.getTransaction(transactionId);

        return transaction.map(value -> new ResponseResult<>(value, HttpStatus.OK, null)).orElseGet(() -> new ResponseResult<>(null, HttpStatus.NOT_FOUND, null));
    }

}
