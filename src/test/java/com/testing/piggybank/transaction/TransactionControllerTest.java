package com.testing.piggybank.transaction;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.testing.piggybank.model.Currency;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetTransactions() throws Exception {
        // Arrange
        long accountId = 1L;

        // Act and Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/transactions/{accountId}", accountId)
                        .param("limit", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.transactions").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.transactions[0].id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.transactions[0].description").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.transactions[0].dateTime").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.transactions[0].amount").exists());
    }

    @Test
    void testCreateTransaction() throws Exception {
        // Arrange
        CreateTransactionRequest createTransactionRequest = new CreateTransactionRequest();
        createTransactionRequest.setSenderAccountId(1L);
        createTransactionRequest.setReceiverAccountId(2L);
        createTransactionRequest.setAmount(BigDecimal.TEN);
        createTransactionRequest.setCurrency(Currency.EURO);
        createTransactionRequest.setDescription("Test Transaction");

        // Act and Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createTransactionRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
