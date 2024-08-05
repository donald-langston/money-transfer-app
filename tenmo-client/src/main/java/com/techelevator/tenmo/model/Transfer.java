package com.techelevator.tenmo.model;

import java.math.BigDecimal;

public class Transfer {
    private int id;
    private String transferType;
    private String transferStatus;
    private int accountFrom;
    private int accountTo;
    private BigDecimal amount;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTransferType() {
        return transferType;
    }

    public void setTransferType(String transferType) {
        this.transferType = transferType;
    }

    public String getTransferStatus() {
        return transferStatus;
    }

    public void setTransferStatus(String transferStatus) {
        this.transferStatus = transferStatus;
    }

    public int getAccountFrom() {
        return accountFrom;
    }

    public void setAccountFrom(int accountFrom) {
        this.accountFrom = accountFrom;
    }

    public int getAccountTo() {
        return accountTo;
    }

    public void setAccountTo(int accountTo) {
        this.accountTo = accountTo;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }


    @Override
    public String toString() {
        return "\n"  +
                "transferId: " + id + "\n" +
                "transferType: " + transferType + "\n" +
                "transferStatus: '" + transferStatus + '\'' + "\n" +
                "accountFrom: " + accountFrom + "\n" +
                "accountTo: " + accountTo + "\n" +
                "amount: " + amount;
    }

}
