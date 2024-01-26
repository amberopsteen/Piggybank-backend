package com.testing.piggybank.account;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetAccount() throws Exception {
        // Arrange
        long accountId = 1L;

        // Act and Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/accounts/{accountId}", accountId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(accountId));
    }

    @Test
    void testGetAccounts() throws Exception {
        // Arrange
        long userId = 123L;

        // Act and Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/accounts")
                        .header("X-User-Id", userId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.accounts").isArray());
    }
}
