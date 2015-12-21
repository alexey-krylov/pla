package com.pla.core.domain.model.generalinformation;

import com.pla.sharedkernel.domain.model.ReinstatementInterestType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Created by Admin on 16-Dec-15.
 */
@Getter
@Setter
@NoArgsConstructor
public class ReinstatementInterest {
    private ReinstatementInterestType reinstatementInterestType;
    private BigDecimal interest;
}
