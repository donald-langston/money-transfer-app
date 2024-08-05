package com.techelevator.tenmo.model;

import java.math.BigDecimal;

public class TransferDto {
    private int accountFromId;
    private int accountToId;
    private BigDecimal amount;

    public TransferDto() {

    }

    public TransferDto(int accountFromId, int accountToId, BigDecimal amount) {
        this.accountFromId = accountFromId;
        this.accountToId = accountToId;
        this.amount = amount;
    }

    public int getAccountFromId() {
        return accountFromId;
    }

    public void setAccountFromId(int accountFromId) {
        this.accountFromId = accountFromId;
    }

    public int getAccountToId() {
        return accountToId;
    }

    public void setAccountToId(int accountToId) {
        this.accountToId = accountToId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
