package com.pla.sharedkernel.identifier;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serializable;

/**
 * Created by Samir on 4/5/2015.
 */
@Getter
@EqualsAndHashCode
public class PremiumId implements Serializable {

    private String id;

    public PremiumId(String id) {
        this.id = id;
    }
}
