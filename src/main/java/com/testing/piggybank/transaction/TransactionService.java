package com.testing.piggybank.transaction;

import com.testing.piggybank.account.AccountService;
import com.testing.piggybank.helper.CurrencyConverterService;
import com.testing.piggybank.model.Account;
import com.testing.piggybank.model.Direction;
import com.testing.piggybank.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * This service is responsible for:
 * <ul>
 *     <li>Get transactions (transfers)</li>
 *     <li>Create transaction (transfer from one account to another)</li>
 * </ul>
 */
@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CurrencyConverterService converterService;
    private final AccountService accountService;

    @Autowired
    public TransactionService(final TransactionRepository transactionRepository, final CurrencyConverterService converterService, final AccountService accountService) {
        this.transactionRepository = transactionRepository;
        this.converterService = converterService;
        this.accountService = accountService;
    }

    /**
     * Get transactions for given account ID.
     *
     * @param limit     Limit the list of transaction (max results).
     * @param accountId The ID of the accounts the transaction are requested for.
     * @return List of {@link Transaction}
     */
    public List<Transaction> getTransactions(final Integer limit, final long accountId) {
        // Get all transactions from database.
        final List<Transaction> result = new ArrayList<>();
        transactionRepository.findAll().forEach(result::add);

        return filterAndLimitTransactions(result, accountId, limit).stream().sorted(TransactionService::sortDescByDateTime).toList();
    }

    /**
     * Filters transactions that only belongs to given account ID and limits (max size) results.
     *
     * @param transactions The list of transactions that needs to be filtered
     * @param accountId    account ID that
     * @param limit        max results
     * @return list of {@link Transaction}
     */
    public List<Transaction> filterAndLimitTransactions(final List<Transaction> transactions, final long accountId, final Integer limit) {
        // Get all transactions that belong to this accountId.
        final List<Transaction> transactionsForAccount = transactions.stream().filter(transaction -> transaction.getReceiverAccount().getId() == accountId || transaction.getSenderAccount().getId() == accountId).toList();

        // No transactions left for account.
        if (transactionsForAccount.size() == 0) {
            return transactionsForAccount;
        }

        // When limit it requested.
        if (limit != null) {
            final int startIndex = limit > transactionsForAccount.size() ? 0 : transactionsForAccount.size() - limit;
            return transactionsForAccount.subList(startIndex, transactionsForAccount.size());
        }

        return transactionsForAccount;
    }

    /**
     * Create and saves the transaction.
     *
     * @param request {@link CreateTransactionRequest}
     */
    public void createTransaction(final CreateTransactionRequest request) {
        final Transaction transaction = mapRequestToTransaction(request);

        // Convert the currency to euro.
        final BigDecimal amountInEuro = converterService.toEuro(transaction.getCurrency(), transaction.getAmount());
        transaction.setAmount(amountInEuro);

        // Update balances
        long fromAccountId = transaction.getSenderAccount().getId();
        accountService.updateBalance(fromAccountId, transaction.getAmount(), Direction.CREDIT);

        long toAccountId = transaction.getReceiverAccount().getId();
        accountService.updateBalance(toAccountId, transaction.getAmount(), Direction.DEBIT);

        // Save the transaction.
        transactionRepository.save(transaction);
    }

    public static int sortDescByDateTime(final Transaction t1, final Transaction t2) {
        return t2.getDateTime().compareTo(t1.getDateTime());
    }

    Transaction mapRequestToTransaction(final CreateTransactionRequest request) {
        // Find the accounts that belong to given ID's
        final Account senderAccount = accountService.getAccount(request.getSenderAccountId()).orElseThrow(RuntimeException::new);
        final Account receiverAccount = accountService.getAccount(request.getReceiverAccountId()).orElseThrow(RuntimeException::new);

        // Map to transaction so it can be persisted
        final Transaction transaction = new Transaction();
        transaction.setAmount(request.getAmount());
        transaction.setCurrency(request.getCurrency());
        transaction.setDescription(request.getDescription());
        transaction.setSenderAccount(senderAccount);
        transaction.setReceiverAccount(receiverAccount);
        transaction.setDateTime(Instant.now());
        return transaction;
    }
}