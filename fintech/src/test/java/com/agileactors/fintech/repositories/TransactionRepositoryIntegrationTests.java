package com.agileactors.fintech.repositories;

import lombok.extern.java.Log;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

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

    //@Test
    public void testThatTransactionCanBeCreatedAndRecalled(){

    }
}
