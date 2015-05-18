package com.pla.sharedkernel.domain.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Getter;

/**
 * @author: pradyumna
 * @since 1.0 12/03/2015
 */
@Getter
@JsonSerialize(using = ToStringSerializer.class)
public class EndorsementType {

    private String description;

    public EndorsementType() {
    }

    public EndorsementType(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return this.description;
    }
}
