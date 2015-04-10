package com.pla.sharedkernel.identifier;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * Created by User on 4/1/2015.
 */
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommissionId implements Serializable{

    String commissionId;

    public CommissionId(String s) {
        this.commissionId = s;
    }
}
