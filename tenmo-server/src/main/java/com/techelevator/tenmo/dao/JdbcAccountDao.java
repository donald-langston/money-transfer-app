package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.Account;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class JdbcAccountDao implements AccountDao{

    private final JdbcTemplate jdbcTemplate;

    public JdbcAccountDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Account findAccountById(int id) {
        Account account = null;
        String sql = "SELECT account_id, user_id, balance FROM account WHERE account_id = ?;";

        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);

            if (results.next()) {
                account = mapRowToAccount(results);
            }

        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return account;
    }

    @Override
    public Account findAccountByUserId(int userId) {
        Account account = null;
        String sql = "SELECT account_id, user_id, balance FROM account WHERE user_id = ?;";

        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);

            if (results.next()) {
                account = mapRowToAccount(results);
            }

        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return account;
    }

    public Account retrieveAccountBalanceByUserId(int userId) {
        Account account = null;
        String sql = "SELECT account_id, user_id, balance FROM account WHERE user_id = ?;";

        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);

            if (results.next()) {
                account = mapRowToAccount(results);
            }

        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return account;
    }

    @Override
    public void updateBalance(int accountId, BigDecimal balance) {
        Account updatedAccount = null;
        String sql = "UPDATE account SET balance = ? " +
                "WHERE account_id = ?;";

        try {
            int numberOfRows = jdbcTemplate.update(sql, balance, accountId);

            if (numberOfRows == 0) {
                throw new DaoException("Zero rows affected, expected at least one");
            } else {
                updatedAccount = findAccountById(accountId);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
    }

    private Account mapRowToAccount(SqlRowSet results) {
        Account account = new Account();
        account.setId(results.getInt("account_id"));
        account.setUserId(results.getInt("user_id"));
        account.setBalance(results.getBigDecimal("balance"));
        return account;
    }
}
