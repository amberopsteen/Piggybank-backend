package com.testing.piggybank.account;

import com.testing.piggybank.model.Account;
import com.testing.piggybank.model.Direction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Service responsible for:
 * <ul>
 *     <li>Get account for account ID.</li>
 *     <li>Get accounts for user ID.</li>
 *     <li>Update balance for specific account.</li>
 * </ul>
 */
@Service
public class AccountService {
    private final AccountRepository accountRepository;

    @Autowired
    public AccountService(final AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
     * Get specific account information of given account id.
     *
     * @param accountId The ID of the requested account
     * @return Optional {@link Account}
     */
    public Optional<Account> getAccount(final long accountId) {
        return accountRepository.findById(accountId);
    }

    /**
     * Get all accounts of given user id.
     *
     * @param userId The ID of user
     * @return Optional {@link Account}
     */
    public List<Account> getAccountsByUserId(long userId) {
        return accountRepository.findAllByUserId(userId);
    }

    /**
     * Update balance of given accountId
     *
     * @param accountId The account id that needs to be updated
     * @param amount    The amount that needs to be added or subtracted.
     * @param direction {@link Direction}
     */
    public void updateBalance(final long accountId, final BigDecimal amount, final Direction direction) {
        // Get account to update
        final Account accountToUpdate = getAccount(accountId).orElseThrow(RuntimeException::new);

        // If credited, the amount is subtracted from account.
        final BigDecimal amountToUpdate = direction.equals(Direction.CREDIT) ? amount.negate() : amount;

        // Set new balance
        final BigDecimal currentBalance = accountToUpdate.getBalance();
        final BigDecimal newBalance = currentBalance.add(amountToUpdate);
        accountToUpdate.setBalance(newBalance);

        // Save in DB.
        accountRepository.save(accountToUpdate);
    }
}