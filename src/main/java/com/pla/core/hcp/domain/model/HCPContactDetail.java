package com.pla.core.hcp.domain.model;

import lombok.*;
import org.hibernate.annotations.Immutable;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import javax.persistence.Embeddable;

/**
 * Created by Mohan Sharma on 12/17/2015.
 */
@ValueObject
@Immutable
@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class HCPContactDetail {
    private String emailId;
    private String workPhoneNumber;
}
