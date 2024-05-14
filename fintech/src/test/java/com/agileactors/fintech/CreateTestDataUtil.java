package com.agileactors.fintech;

import com.agileactors.fintech.domain.entities.Account;
import com.agileactors.fintech.domain.entities.Transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

public class CreateTestDataUtil {

    public static Account createTestAccountA() {
        return Account.builder()
                .id("111")
                .balance(new BigDecimal(10))
                .currency("USD")
                .createdAt(LocalDateTime.of(2024, 04, 25, 8, 30, 25))
                .build();
    }

    public static Account createTestAccountB() {
        return Account.builder()
                .id("222")
                .balance(new BigDecimal(99))
                .currency("USD")
                .createdAt(LocalDateTime.of(2024, 04, 25, 8, 35, 25))
                .build();
    }

    public static Transaction createTestTransactionA(Account accountA, Account accountB) {
        return Transaction.builder()
                .id("AAA")
                .sourceAccountId(accountA.getId())
                .targetAccountId(accountB.getId())
                .amount(new BigDecimal(5))
                .currency("USD")
                .executedAt(LocalDateTime.of(2024, 05, 15, 8, 35, 25))
                .accounts(Set.of(accountA, accountB))
                .build();
    }
}
