package com.agileactors.fintech.controllers;

import com.agileactors.fintech.CreateTestDataUtil;
import com.agileactors.fintech.domain.entities.Account;
import com.agileactors.fintech.domain.entities.Transaction;
import com.agileactors.fintech.enums.HttpStatusDescr;
import com.agileactors.fintech.enums.ResponseStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class TransactionControllerIntegrationTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Autowired
    public TransactionControllerIntegrationTest(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    @Test
    public void testThatCreateTransactionReturnsBadRequestWhenAccountsDoNotExist() throws Exception {
        Account sourceAccount = CreateTestDataUtil.createTestAccountA();
        Account targetAccount = CreateTestDataUtil.createTestAccountB();

        Transaction testTransactionA = CreateTestDataUtil.createTestTransactionA(sourceAccount, targetAccount);
        String testTransactionAJson = objectMapper.writeValueAsString(testTransactionA);

        mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(testTransactionAJson)
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.status").value(HttpStatusDescr.BAD_REQUEST.name()))
        .andExpect(
                MockMvcResultMatchers.jsonPath("$.message")
                        .value(ResponseStatus.BOTH_SOURCE_AND_TARGET_ACCOUNTS_DO_NOT_EXIST.getDescription())
        );
    }

    @Test
    public void testThatGetTransactionReturnsHttpStatus404WhenAnyTransactionExists() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/transfers/zzzzzzzzzzzzzzzzzz")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$.status").value(HttpStatusDescr.NOT_FOUND.name()));
    }
}
