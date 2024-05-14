package com.agileactors.fintech.validations;

import com.agileactors.fintech.domain.entities.Account;
import com.agileactors.fintech.domain.entities.Transaction;
import com.agileactors.fintech.enums.ResponseStatus;
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
        if (!resultAccountsExistence.isBlank()) return resultAccountsExistence;

        String resultAccountsAreTheSame = validateAccountsAreDifferent(transaction);
        if (!resultAccountsAreTheSame.isBlank()) return resultAccountsAreTheSame;

        String resultCurrencyOfAccountsAndTransactionAreTheSame = validateCurrencyOfAccountsAndTransactionAreTheSame(transaction);
        if (!resultCurrencyOfAccountsAndTransactionAreTheSame.isBlank())
            return resultCurrencyOfAccountsAndTransactionAreTheSame;

        String resultBalanceSufficiency = validateTransactionAmount(transaction);
        if (!resultBalanceSufficiency.isBlank()) return resultBalanceSufficiency;
        return "";
    }

    public String validateAccountsExistence(Transaction transaction) {

        Optional<Account> sourceAccount = accountService.getAccount(transaction.getSourceAccountId());

        Optional<Account> targetAccount = accountService.getAccount(transaction.getTargetAccountId());

        return createResponseMessageForAccountExistence(sourceAccount, targetAccount);
    }

    public String validateAccountsAreDifferent(Transaction transaction) {
        return transaction.getSourceAccountId().equals(transaction.getTargetAccountId()) ?
                ResponseStatus.SOURCE_AND_TARGET_ACCOUNT_ARE_THE_SAME.getDescription()
                : ResponseStatus.EMPTY_STRING.getDescription();
    }

    private String createResponseMessageForAccountExistence(Optional<Account> sourceAccount, Optional<Account> targetAccount) {
        String result;

        if (sourceAccount.isEmpty() && targetAccount.isEmpty()) {
            result = ResponseStatus.BOTH_SOURCE_AND_TARGET_ACCOUNTS_DO_NOT_EXIST.getDescription();
        } else if (sourceAccount.isEmpty() && !targetAccount.isEmpty()) {
            result = ResponseStatus.SOURCE_ACCOUNT_DOES_NOT_EXIST.getDescription();
        } else if (!sourceAccount.isEmpty() && targetAccount.isEmpty()) {
            result = ResponseStatus.TARGET_ACCOUNT_DOES_NOT_EXIST.getDescription();
        } else {
            result = ResponseStatus.EMPTY_STRING.getDescription();
        }
        return result;
    }

    private String validateCurrencyOfAccountsAndTransactionAreTheSame(Transaction transaction) {
        String result;

        Optional<Account> sourceAccount = accountService.getAccount(transaction.getSourceAccountId());
        Optional<Account> targetAccount = accountService.getAccount(transaction.getTargetAccountId());

        String resultCurrencyOfAccounts = createResponseMessageForAccountCurrency(sourceAccount, targetAccount);

        if (resultCurrencyOfAccounts.isBlank()) {
            result = sourceAccount.get().getCurrency().equals(transaction.getCurrency()) ? ResponseStatus.EMPTY_STRING.getDescription()
                    : ResponseStatus.CURRENCY_OF_TRANSACTION_DIFFERS_TO_THAT_OF_ACCOUNTS.getDescription();
        } else {
            result = resultCurrencyOfAccounts;
        }

        return result;
    }

    private String createResponseMessageForAccountCurrency(Optional<Account> sourceAccount, Optional<Account> targetAccount) {

        return sourceAccount.get().getCurrency().equals(targetAccount.get().getCurrency()) ? ResponseStatus.EMPTY_STRING.getDescription()
                : ResponseStatus.SOURCE_AND_TARGET_ACCOUNTS_CURRENCY_ARE_DIFFERENT.getDescription();
    }


    public String validateTransactionAmount(Transaction transaction) {

        Optional<Account> sourceAccount = accountService.getAccount(transaction.getSourceAccountId());

        return checkCalculatedTransferAmount(transaction, sourceAccount);

    }

    private String checkCalculatedTransferAmount(Transaction transaction, Optional<Account> sourceAccountOptional) {

        BigDecimal sourceAccountBalance = sourceAccountOptional.get().getBalance();

        String result = ResponseStatus.EMPTY_STRING.getDescription();

        if (transaction.getAmount().compareTo(BigDecimal.valueOf(0)) <= 0) {
            result = ResponseStatus.TRANSACTION_AMOUNT_IS_NOT_VALID.getDescription();
        } else if (sourceAccountBalance.compareTo(transaction.getAmount()) <= 0) {
            result = ResponseStatus.INSUFFICIENT_BALANCE_FOR_MONEY_TRANSFER.getDescription();
        }
        return result;
    }

}
