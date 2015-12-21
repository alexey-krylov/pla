package com.pla.sharedkernel.domain.model;

import lombok.Getter;

/**
 * Created by Admin on 16-Dec-15.
 */
@Getter
public enum SurrenderChargesType {

    AMOUNT("Amount"),PERCENTAGE_OF_SURRENDER_AMOUNT("% of Surrender Amount");

    private String description;

    SurrenderChargesType(String description){
        this.description = description;
    }

    @Override
    public String toString() {
        return  description;
    }
}
