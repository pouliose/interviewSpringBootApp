package com.agileactors.fintech.controllers;

import com.agileactors.fintech.CreateTestDataUtil;
import com.agileactors.fintech.domain.entities.Account;
import com.agileactors.fintech.domain.entities.Transaction;
import com.agileactors.fintech.enums.HttpStatusDescr;
import com.agileactors.fintech.enums.ResponseStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import lombok.extern.java.Log;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;

@SpringBootTest
@Log
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class TransactionControllerIntegrationTest {

    private final MockMvc mockMvc;

    private final ObjectMapper objectMapper;

    @Autowired
    public TransactionControllerIntegrationTest(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    @Test
    public void testThatCreateTransactionReturnsHttp201CreatedWhenAccountsExist() throws Exception {

        Account sourceAccount = CreateTestDataUtil.createTestAccountA();

        String sourceAccountJson = objectMapper.writeValueAsString(sourceAccount);

        MvcResult sourceResult = mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(sourceAccountJson)
        ).andReturn();

        String sourceAccountId = JsonPath.read(sourceResult.getResponse().getContentAsString(), "$.data.id");
        sourceAccount.setId(sourceAccountId);

        Account targetAccount = CreateTestDataUtil.createTestAccountB();
        String targetAccountJson = objectMapper.writeValueAsString(targetAccount);

        MvcResult targetResult = mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(targetAccountJson)
        ).andReturn();

        String targetAccountId = JsonPath.read(targetResult.getResponse().getContentAsString(), "$.data.id");
        targetAccount.setId(targetAccountId);

        Transaction testTransactionA = CreateTestDataUtil.createTestTransactionA(sourceAccountId, targetAccountId);
        String testTransactionAJson = objectMapper.writeValueAsString(testTransactionA);

        mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(sourceAccountJson)
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.data.id").isString()
        );

        mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(testTransactionAJson)
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.status").value(HttpStatusDescr.CREATED.name()));
    }

    @Test
    public void testThatCreateTransactionReturnsHttpBadRequestWhenSourceAccountDoesNotExist() throws Exception {

        Account targetAccount = CreateTestDataUtil.createTestAccountB();
        String targetAccountJson = objectMapper.writeValueAsString(targetAccount);

        MvcResult targetResult = mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(targetAccountJson)
        ).andReturn();

        String targetAccountId = JsonPath.read(targetResult.getResponse().getContentAsString(), "$.data.id");
        targetAccount.setId(targetAccountId);

        Transaction testTransactionA = CreateTestDataUtil.createTestTransactionA("sourceAccountIdNotExist", targetAccountId);
        String testTransactionAJson = objectMapper.writeValueAsString(testTransactionA);

        mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(testTransactionAJson)
                ).andExpect(
                        MockMvcResultMatchers.jsonPath("$.status").value(HttpStatusDescr.BAD_REQUEST.name()))
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$.message").value(ResponseStatus.SOURCE_ACCOUNT_DOES_NOT_EXIST.getDescription())
                );
    }

    @Test
    public void testThatCreateTransactionReturnsHttpBadRequestWhenTargetAccountDoesNotExist() throws Exception {

        Account sourceAccount = CreateTestDataUtil.createTestAccountA();

        String sourceAccountJson = objectMapper.writeValueAsString(sourceAccount);

        MvcResult sourceResult = mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(sourceAccountJson)
        ).andReturn();

        String sourceAccountId = JsonPath.read(sourceResult.getResponse().getContentAsString(), "$.data.id");
        sourceAccount.setId(sourceAccountId);

        mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(sourceAccountJson)
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.data.id").isString()
        );

        Transaction testTransactionA = CreateTestDataUtil.createTestTransactionA(sourceAccountId, "targetAccountIdDoNotExist");
        String testTransactionAJson = objectMapper.writeValueAsString(testTransactionA);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/api/transfers")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(testTransactionAJson)
                ).andExpect(
                        MockMvcResultMatchers.jsonPath("$.status").value(HttpStatusDescr.BAD_REQUEST.name()))
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$.message").value(ResponseStatus.TARGET_ACCOUNT_DOES_NOT_EXIST.getDescription())
                );
    }

    @Test
    public void testThatCreateTransactionReturnsHttpBadRequestWhenSourceAccountHasInsufficientBalance() throws Exception {

        Account sourceAccount = CreateTestDataUtil.createTestAccountA();

        String sourceAccountJson = objectMapper.writeValueAsString(sourceAccount);

        MvcResult sourceResult = mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(sourceAccountJson)
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.status").value(HttpStatusDescr.CREATED.name())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.data.id").isString()
        ).andReturn();

        String sourceAccountId = JsonPath.read(sourceResult.getResponse().getContentAsString(), "$.data.id");
        sourceAccount.setId(sourceAccountId);

        Account targetAccount = CreateTestDataUtil.createTestAccountB();
        String targetAccountJson = objectMapper.writeValueAsString(targetAccount);

        MvcResult targetResult = mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(targetAccountJson)
        ).andReturn();

        String targetAccountId = JsonPath.read(targetResult.getResponse().getContentAsString(), "$.data.id");
        targetAccount.setId(targetAccountId);

        Transaction testTransactionA = CreateTestDataUtil.createTestTransactionA(sourceAccountId, targetAccountId);
        testTransactionA.setAmount(sourceAccount.getBalance().add(BigDecimal.valueOf(1)));

        String testTransactionAJson = objectMapper.writeValueAsString(testTransactionA);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/api/transfers")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(testTransactionAJson)
                ).andExpect(
                        MockMvcResultMatchers.jsonPath("$.status").value(HttpStatusDescr.BAD_REQUEST.name()))
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$.message").value(ResponseStatus.INSUFFICIENT_BALANCE_FOR_MONEY_TRANSFER.getDescription())
                );
    }

    @Test
    public void testThatCreateTransactionReturnsHttpBadRequestWhenTransactionAmountIsZero() throws Exception {

        Account sourceAccount = CreateTestDataUtil.createTestAccountA();

        String sourceAccountJson = objectMapper.writeValueAsString(sourceAccount);

        MvcResult sourceResult = mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(sourceAccountJson)
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.status").value(HttpStatusDescr.CREATED.name())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.data.id").isString()
        ).andReturn();

        String sourceAccountId = JsonPath.read(sourceResult.getResponse().getContentAsString(), "$.data.id");
        sourceAccount.setId(sourceAccountId);

        Account targetAccount = CreateTestDataUtil.createTestAccountB();
        String targetAccountJson = objectMapper.writeValueAsString(targetAccount);

        MvcResult targetResult = mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(targetAccountJson)
        ).andReturn();

        String targetAccountId = JsonPath.read(targetResult.getResponse().getContentAsString(), "$.data.id");
        targetAccount.setId(targetAccountId);

        Transaction testTransactionA = CreateTestDataUtil.createTestTransactionA(sourceAccountId, targetAccountId);
        testTransactionA.setAmount(BigDecimal.valueOf(0));

        String testTransactionAJson = objectMapper.writeValueAsString(testTransactionA);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/api/transfers")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(testTransactionAJson)
                ).andExpect(
                        MockMvcResultMatchers.jsonPath("$.status").value(HttpStatusDescr.BAD_REQUEST.name()))
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$.message").value(ResponseStatus.TRANSACTION_AMOUNT_IS_NOT_VALID.getDescription())
                );
    }

    @Test
    public void testThatCreateTransactionReturnsHttpBadRequestWhenSourceAndTargetAccountsAreTheSame() throws Exception {

        Account sourceAccount = CreateTestDataUtil.createTestAccountA();

        String sourceAccountJson = objectMapper.writeValueAsString(sourceAccount);

        MvcResult sourceResult = mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(sourceAccountJson)
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.status").value(HttpStatusDescr.CREATED.name())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.data.id").isString()
        ).andReturn();

        String sourceAccountId = JsonPath.read(sourceResult.getResponse().getContentAsString(), "$.data.id");
        sourceAccount.setId(sourceAccountId);

        Transaction testTransactionA = CreateTestDataUtil.createTestTransactionA(sourceAccountId, sourceAccountId);

        String testTransactionAJson = objectMapper.writeValueAsString(testTransactionA);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/api/transfers")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(testTransactionAJson)
                ).andExpect(
                        MockMvcResultMatchers.jsonPath("$.status").value(HttpStatusDescr.BAD_REQUEST.name()))
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$.message").value(ResponseStatus.SOURCE_AND_TARGET_ACCOUNT_ARE_THE_SAME.getDescription())
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
