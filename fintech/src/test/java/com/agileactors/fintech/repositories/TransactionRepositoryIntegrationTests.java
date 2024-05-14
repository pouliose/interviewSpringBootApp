package com.agileactors.fintech.repositories;

import com.agileactors.fintech.CreateTestDataUtil;
import com.agileactors.fintech.domain.entities.Account;
import com.agileactors.fintech.domain.entities.Transaction;
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
    public void testThatTransactionCanBeCreatedAndRecalled(){
        Account accountA = CreateTestDataUtil.createTestAccountA();
        Account accountB = CreateTestDataUtil.createTestAccountB();

        log.info("Account!:" + accountA.toString());

        Transaction transaction = CreateTestDataUtil.createTestTransactionA(accountA, accountB);
        transaction.setAccounts(Set.of(accountA, accountB));
        transactionRepository.save(transaction);

        Optional<Transaction> result = transactionRepository.findById(transaction.getId());
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(transaction);
    }
}
