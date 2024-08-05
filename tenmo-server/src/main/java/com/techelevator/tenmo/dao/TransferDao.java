package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferStatus;

import java.util.List;

public interface TransferDao {

    Transfer findTransferById(int id);

    List<Transfer> findTransfersByUserId(int userId);

    Transfer saveTransfer(Transfer transfer);

    void updateTransferStatus(int transferId, TransferStatus status);
}
