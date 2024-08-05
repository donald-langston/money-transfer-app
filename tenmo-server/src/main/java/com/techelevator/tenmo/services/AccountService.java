package com.techelevator.tenmo.services;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.exception.*;
import com.techelevator.tenmo.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.security.auth.login.AccountNotFoundException;
import java.math.BigDecimal;

@Service
public class AccountService {
    private final AccountDao accountDao;

    @Autowired
    public AccountService(AccountDao accountDao) {this.accountDao = accountDao;}

    public Account findAccountById(int accountId) {
        try {
            Account account = accountDao.findAccountById(accountId);
            if (account == null) {
                throw new AccountNotFoundException("Account not found with provided Id");
            }
            return account;
        } catch (DaoException e) {
            throw new DaoException("Server error occurred", e);
        } catch (AccountNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public Account findAccountByUserId(int userId) {
        try {
            Account account = accountDao.findAccountByUserId(userId);
            if (account == null) {
                throw new AccountNotFoundException("Account not found with associated Id");
            }
            return account;
        } catch (DaoException e) {
            throw new DaoException("Server error occurred", e);
        } catch (AccountNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public BigDecimal retrieveAccountBalance(int userId) throws AccountNotFoundException {
        Account account = accountDao.retrieveAccountBalanceByUserId(userId);
        if (account == null) {
            throw new AccountNotFoundException("Not able to view balance of requested account");
        }
        return account.getBalance();
    }

    public boolean hasSufficientFunds(int accountId, BigDecimal amount) throws AccountNotFoundException {
        Account account = accountDao.findAccountById(accountId);

        if (account == null) {
            throw new AccountNotFoundException("Account not found");
        }

        return account.getBalance().compareTo(amount) >= 0;
    }

    @Transactional
    public void withdraw(int accountId, BigDecimal amount) throws AccountNotFoundException {
        Account account = accountDao.findAccountById(accountId);

        if (account == null) {
            throw new AccountNotFoundException("Account not found");
        }

        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds for withdrawal");
        }

        accountDao.updateBalance(accountId, account.getBalance().subtract(amount));
    }

    @Transactional
    public void deposit(int accountId, BigDecimal amount) throws AccountNotFoundException {
        Account account = accountDao.findAccountById(accountId);

        if (account == null) {
            throw new AccountNotFoundException("Account not found");
        }

        accountDao.updateBalance(accountId, account.getBalance().add(amount));
    }
}
