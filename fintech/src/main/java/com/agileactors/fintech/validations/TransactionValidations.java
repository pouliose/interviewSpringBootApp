package com.agileactors.fintech.validations;

import com.agileactors.fintech.domain.entities.Account;
import com.agileactors.fintech.domain.entities.Transaction;
import com.agileactors.fintech.services.AccountService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
public class TransactionValidations {

    private final AccountService accountService;

    public String validateTransaction(Transaction transaction) {

        String resultAccountsExistence = validateAccountsExistence(transaction);
        if(!resultAccountsExistence.isBlank()) return resultAccountsExistence;

        String resultAccountsAreTheSame = validateAccountsAreDifferent(transaction);
        if(!resultAccountsAreTheSame.isBlank()) return resultAccountsAreTheSame;

        String resultCurrencyOfAccountsAndTransactionAreTheSame = validateCurrencyOfAccountsAndTransactionAreTheSame(transaction);
        if(!resultCurrencyOfAccountsAndTransactionAreTheSame.isBlank()) return resultCurrencyOfAccountsAndTransactionAreTheSame;

        String resultBalanceSufficiency = validateTransactionAmount(transaction);
        if(!resultBalanceSufficiency.isBlank()) return resultBalanceSufficiency;
        return "";
    }

    public String validateAccountsExistence(Transaction transaction) {

        Optional<Account> sourceAccount = accountService.getAccount(transaction.getSourceAccountId());

        Optional<Account> targetAccount = accountService.getAccount(transaction.getTargetAccountId());

        return createResponseMessageForAccountExistence(sourceAccount, targetAccount);
    }

    public String validateAccountsAreDifferent(Transaction transaction) {
        return transaction.getSourceAccountId().equals(transaction.getTargetAccountId()) ? "Source and target account are the same." : "";
    }

    private String createResponseMessageForAccountExistence(Optional<Account> sourceAccount, Optional<Account> targetAccount) {
        String result;

        if (sourceAccount.isEmpty() && targetAccount.isEmpty()) {
            result = "Both source and target accounts do not exist.";
        } else if (sourceAccount.isEmpty() && !targetAccount.isEmpty()) {
            result = "Source account does not exist.";
        } else if (!sourceAccount.isEmpty() && targetAccount.isEmpty()) {
            result = "Target account does not exist.";
        } else {
            result = "";
        }
        return result;
    }

    private String validateCurrencyOfAccountsAndTransactionAreTheSame(Transaction transaction) {
        String result;

        Optional<Account> sourceAccount = accountService.getAccount(transaction.getSourceAccountId());
        Optional<Account> targetAccount = accountService.getAccount(transaction.getTargetAccountId());

        String resultCurrencyOfAccounts = createResponseMessageForAccountCurrency(sourceAccount, targetAccount);

        if(resultCurrencyOfAccounts.isBlank()){
            result = sourceAccount.get().getCurrency().equals(transaction.getCurrency()) ? "" : "Currency of transaction differs to that of accounts";
        } else {
            result = resultCurrencyOfAccounts;
        }

        return result;
    }

    private String createResponseMessageForAccountCurrency(Optional<Account> sourceAccount, Optional<Account> targetAccount) {

        return sourceAccount.get().getCurrency().equals(targetAccount.get().getCurrency()) ? "" : "Source and target accounts currency are different";
    }


    public String validateTransactionAmount(Transaction transaction) {

        Optional<Account> sourceAccount = accountService.getAccount(transaction.getSourceAccountId());

        return checkCalculatedTransferAmount(transaction, sourceAccount);

    }

    private String checkCalculatedTransferAmount(Transaction transaction, Optional<Account> sourceAccountOptional) {

        BigDecimal sourceAccountBalance = sourceAccountOptional.get().getBalance();

        if (sourceAccountBalance.compareTo(transaction.getAmount()) < 0) {
            return "Insufficient  balance for money transfer";
        }
        return "";
    }

}
