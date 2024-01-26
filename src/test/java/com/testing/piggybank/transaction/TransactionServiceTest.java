package com.testing.piggybank.transaction;

import static org.mockito.Mockito.*;

import com.testing.piggybank.account.AccountService;
import com.testing.piggybank.helper.CurrencyConverterService;
import com.testing.piggybank.model.Account;
import com.testing.piggybank.model.Currency;
import com.testing.piggybank.model.Direction;
import com.testing.piggybank.model.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CurrencyConverterService converterService;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetTransactions() {
        // Arrange
        long accountId = 1L;
        Integer limit = 5;
        List<Transaction> mockTransactions = createMockTransactionList();
        when(transactionRepository.findAll()).thenReturn(mockTransactions);

        // Act
        List<Transaction> result = transactionService.getTransactions(limit, accountId);

        // Assert
        assertNotNull(result);
        verify(transactionRepository, times(1)).findAll();
    }

    @Test
    void testCreateTransaction() {
        // Arrange
        CreateTransactionRequest mockRequest = createMockCreateTransactionRequest();
        Transaction mockTransaction = createMockTransaction();
        Account mockSenderAccount = createMockAccount();
        Account mockReceiverAccount = createMockAccount();

        when(accountService.getAccount(anyLong())).thenReturn(Optional.of(mockSenderAccount), Optional.of(mockReceiverAccount));
        when(converterService.toEuro(any(Currency.class), any(BigDecimal.class))).thenReturn(BigDecimal.TEN);
        doNothing().when(accountService).updateBalance(anyLong(), any(BigDecimal.class), any(Direction.class));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(mockTransaction);

        // Act
        transactionService.createTransaction(mockRequest);

        // Assert
        verify(accountService, times(2)).getAccount(anyLong());
        verify(converterService, times(1)).toEuro(any(Currency.class), any(BigDecimal.class));
        verify(accountService, times(2)).updateBalance(anyLong(), any(BigDecimal.class), any(Direction.class));
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    private List<Transaction> createMockTransactionList() {
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(createMockTransaction());
        transactions.add(createMockTransaction());
        return transactions;
    }

    private Transaction createMockTransaction() {
        Transaction transaction = new Transaction();
        transaction.setId(1L);
        transaction.setAmount(BigDecimal.TEN);
        transaction.setCurrency(Currency.EURO);
        transaction.setDateTime(Instant.now());
        transaction.setDescription("Mock Transaction");
        transaction.setSenderAccount(createMockAccount());
        transaction.setReceiverAccount(createMockAccount());
        return transaction;
    }

    private CreateTransactionRequest createMockCreateTransactionRequest() {
        CreateTransactionRequest request = new CreateTransactionRequest();
        request.setSenderAccountId(1L);
        request.setReceiverAccountId(2L);
        request.setAmount(BigDecimal.TEN);
        request.setCurrency(Currency.EURO);
        request.setDescription("Mock Transaction");
        return request;
    }

    private Account createMockAccount() {
        Account account = new Account();
        account.setId(1L);
        account.setBalance(BigDecimal.valueOf(1000));
        account.setName("Mock Account");
        account.setId(123L);
        return account;
    }

    @Test
    void testSortDescByDateTime() {
        // Create two mock transactions with different DateTime values
        Transaction transaction1 = mock(Transaction.class);
        when(transaction1.getDateTime()).thenReturn(Instant.parse("2022-01-01T00:00:00Z"));

        Transaction transaction2 = mock(Transaction.class);
        when(transaction2.getDateTime()).thenReturn(Instant.parse("2022-01-02T00:00:00Z"));

        // Call the static method to sort transactions
        int result = TransactionService.sortDescByDateTime(transaction1, transaction2);

        // Assert that the result is negative, indicating a descending order
        assertTrue(result > 0, "Transactions should be sorted in descending order by DateTime");
    }
}
