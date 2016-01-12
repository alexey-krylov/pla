package com.pla.core.hcp.domain.model;

import lombok.*;
import org.hibernate.annotations.Immutable;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import javax.persistence.Embeddable;

/**
 * Author - Mohan Sharma Created on 12/17/2015.
 */
@ValueObject
@Immutable
@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class HCPContactPersonDetail {
    private String contactPersonDetail;
    private String contactPersonMobile;
    private String contactPersonWorkPhoneNumber;
    private String contactPersonEmailId;
}
