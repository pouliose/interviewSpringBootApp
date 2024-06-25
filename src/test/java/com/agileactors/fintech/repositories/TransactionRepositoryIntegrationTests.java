package com.agileactors.fintech.repositories;

import com.agileactors.fintech.CreateTestDataUtil;
import com.agileactors.fintech.domain.entities.Account;
import com.agileactors.fintech.domain.entities.Transaction;
import jakarta.transaction.Transactional;
import lombok.extern.java.Log;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Log
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TransactionRepositoryIntegrationTests {

    private final TransactionRepository transactionRepository;
    @Autowired
    public TransactionRepositoryIntegrationTests(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Test
    public void testThatTransactionCanBeCreatedAndRecalled() {
        Account accountA = CreateTestDataUtil.createTestAccountA();
        Account accountB = CreateTestDataUtil.createTestAccountB();

        Transaction transaction = CreateTestDataUtil.createTestTransactionA(accountA.getId(), accountB.getId());
        transaction.setAccounts(Set.of(accountA, accountB));
        transactionRepository.save(transaction);

        Optional<Transaction> result = transactionRepository.findById(transaction.getId());
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(transaction);
    }

    @Test
    public void testThatMultipleTransactionsCanBeCreatedAndRecalled() {
        Account accountA = CreateTestDataUtil.createTestAccountA();
        Account accountB = CreateTestDataUtil.createTestAccountB();

        Transaction transactionA = CreateTestDataUtil.createTestTransactionA(accountA.getId(), accountB.getId());
        transactionA.setAccounts(Set.of(accountA, accountB));
        transactionRepository.save(transactionA);

        Transaction transactionB = CreateTestDataUtil.createTestTransactionA(accountA.getId(), accountB.getId());
        transactionB.setAccounts(Set.of(accountA, accountB));
        transactionRepository.save(transactionB);

        Iterable<Transaction> result = transactionRepository.findAll();
        result.forEach(System.out::println);
        assertThat(result).hasSize(2).containsExactlyInAnyOrder(transactionA, transactionB);
    }
}
