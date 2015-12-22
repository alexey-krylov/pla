package com.pla.grouplife.claim.domain.model;

/**
 * Created by ak
 */
public enum PaymentMode {

    CHEQUE("Cheque"),TRANSFER("Transfer"),CASH("Cash");

    private String description;

    PaymentMode(String description){
        this.description = description;
    }

    @Override
    public String toString() {
        return  description;
    }
}
