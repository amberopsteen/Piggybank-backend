package com.testing.piggybank.account;

import com.testing.piggybank.model.Account;
import com.testing.piggybank.model.Direction;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    @Test
    void testGetAccount() {
        // Arrange
        long accountId = 1L;
        Account mockAccount = new Account();
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(mockAccount));

        // Act
        Optional<Account> result = accountService.getAccount(accountId);

        // Assert
        assertEquals(Optional.of(mockAccount), result);
        verify(accountRepository, times(1)).findById(accountId);
    }

    @Test
    void testGetAccountsByUserId() {
        // Arrange
        long userId = 1L;
        List<Account> mockAccounts = List.of(new Account(), new Account());
        when(accountRepository.findAllByUserId(userId)).thenReturn(mockAccounts);

        // Act
        List<Account> result = accountService.getAccountsByUserId(userId);

        // Assert
        assertEquals(mockAccounts, result);
        verify(accountRepository, times(1)).findAllByUserId(userId);
    }

    @Test
    void testUpdateBalance() {
        // Arrange
        long accountId = 1L;
        BigDecimal amount = new BigDecimal("100.00");
        Direction direction = Direction.CREDIT;

        Account mockAccount = new Account();
        mockAccount.setBalance(new BigDecimal("500.00"));

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(mockAccount));
        when(accountRepository.save(any(Account.class))).thenReturn(mockAccount);

        // Act
        accountService.updateBalance(accountId, amount, direction);

        // Assert
        BigDecimal expectedBalance = direction.equals(Direction.CREDIT)
                ? new BigDecimal("400.00") // 500 - 100
                : new BigDecimal("600.00"); // 500 + 100
        assertEquals(expectedBalance, mockAccount.getBalance());

        verify(accountRepository, times(1)).findById(accountId);
        verify(accountRepository, times(1)).save(mockAccount);
    }
}
