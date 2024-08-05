package com.techelevator.tenmo.model;

public enum TransferType {
    REQUEST(1, "Request"),
    SEND(2, "Send");

    private final int id;
    private final String description;

    TransferType(int id, String description) {
        this.id = id;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public static TransferType fromId(int id) {
        for (TransferType type : values()) {
            if (type.id == id) {
                return type;
            }
        }

        throw new IllegalArgumentException("Invalid TransferType id: " + id);
    }
}
