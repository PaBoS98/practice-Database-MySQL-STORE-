package com.service.enums;

public enum ProductStatus {
    OUT_OF_STOCK("OUT_OF_STOCK"),
    IN_STOCK("IN_STOCK"),
    RUNNING_LOW("RUNNING_LOW");

    private String status;

    ProductStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return status;
    }
}
