/*
 * Copyright (c) 3/5/15 5:24 PM .NthDimenzionInc - All Rights Reserved
 * Unauthorized copying of this file via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.application;

import com.pla.core.dto.CommissionTermDto;
import com.pla.sharedkernel.identifier.CommissionId;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Set;

/**
 * @author: Nischitha
 * @since 1.0 10/03/2015
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
public class UpdateCommissionCommand {

    CommissionId commissionId;
    Set<CommissionTermDto> commissionTermSet;
    UserDetails userDetails;

}
