package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferType;
import com.techelevator.tenmo.model.TransferStatus;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransferDao implements TransferDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcTransferDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Transfer findTransferById(int id) {
        Transfer transfer = null;
        String sql = "SELECT t.transfer_id, t.transfer_status_id, ts.transfer_status_desc, " +
                "t.transfer_type_id, tt.transfer_type_desc, t.account_from, t.account_to, t.amount " +
                "FROM transfer t " +
                "JOIN transfer_status ts ON ts.transfer_status_id = t.transfer_status_id " +
                "JOIN transfer_type tt ON tt.transfer_type_id = t.transfer_type_id " +
                "WHERE t.transfer_id = ?;";

        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);

            if (results.next()) {
                transfer = mapRowToTransfer(results);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }

        return transfer;
    }

    @Override
    public List<Transfer> findTransfersByUserId(int userId) {
        List<Transfer> transfers = new ArrayList<>();

        String sql = "SELECT t.transfer_id, t.transfer_status_id, ts.transfer_status_desc, t.transfer_type_id, " +
                "tt.transfer_type_desc, t.account_from, t.account_to, t.amount " +
                "FROM transfer t " +
                "JOIN transfer_status ts ON ts.transfer_status_id = t.transfer_status_id " +
                "JOIN transfer_type tt ON tt.transfer_type_id = t.transfer_type_id " +
                "JOIN account af ON t.account_from = af.account_id " +
                "JOIN account at ON t.account_to = at.account_id " +
                "JOIN tenmo_user tu ON af.user_id = tu.user_id OR at.user_id = tu.user_id " +
                "WHERE tu.user_id = ?;";

        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);

            while (results.next()) {
                Transfer transfer = mapRowToTransfer(results);
                transfers.add(transfer);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }

        return transfers;
    }

    @Override
    public Transfer saveTransfer(Transfer transfer) {
        Transfer newTransfer = null;


        String sql = "INSERT INTO transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount) " +
                "VALUES (?, ?, ?, ?, ?) RETURNING transfer_id;";

        try {
            int newTransferId = jdbcTemplate.queryForObject(sql, int.class, transfer.getTransferType().getId(), transfer.getTransferStatus().getId(), transfer.getAccountFrom(), transfer.getAccountTo(), transfer.getAmount());
            newTransfer = findTransferById(newTransferId);
            return newTransfer;
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
    }

    @Override
    public void updateTransferStatus(int transferId, TransferStatus status) {
        Transfer updatedTransfer = null;
        String sql = "UPDATE transfer SET transfer_status_id = ? " +
                "WHERE transfer_id = ?";

        try {
            int numberOfRows = jdbcTemplate.update(sql, status.getId(), transferId);

            if (numberOfRows == 0) {
                throw new DaoException("Zero rows affected, expected at least one");
            } else {
                updatedTransfer = findTransferById(transferId);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
    }

    private Transfer mapRowToTransfer(SqlRowSet results) {
        Transfer transfer = new Transfer();
        transfer.setTransferType(TransferType.fromId(results.getInt("transfer_type_id")));
        transfer.setTransferStatus(TransferStatus.fromId(results.getInt("transfer_status_id")));
        transfer.setId(results.getInt("transfer_id"));
        transfer.setAccountFrom(results.getInt("account_from"));
        transfer.setAccountTo(results.getInt("account_to"));
        transfer.setAmount(results.getBigDecimal("amount"));

        return transfer;
    }
}
