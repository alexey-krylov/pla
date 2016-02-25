package com.pla.core.hcp.domain.model;

import lombok.*;
import org.hibernate.annotations.Immutable;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import javax.persistence.Embeddable;

/**
 * Created by Rudra on 2/24/2016.
 */

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@ValueObject
@Immutable
@Embeddable
@EqualsAndHashCode
public class HCPBankDetail {

    private String bankName;
    private String bankBranchCode;
    private String bankAccountType;
    private String bankAccountNumber;
    private String bankBranchSortCode;

}
