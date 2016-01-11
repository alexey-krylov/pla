package com.pla.grouplife.sharedresource.model.vo;

import com.pla.grouplife.sharedresource.model.UnderWriterType;
import com.pla.sharedkernel.domain.model.PremiumType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import java.math.BigDecimal;

/**
 * Created by Admin on 08-Jan-16.
 */
@ValueObject
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
@Setter(value = AccessLevel.PACKAGE)
public class UnderWriterFactor {
    private PremiumType premiumType;
    private UnderWriterType underWriterType;
    private BigDecimal underWritingLoading;
    private BigDecimal underWriterDiscount;

    public BigDecimal getUnderWritingFactor(){
        return this.underWriterType.getUnderWritingAmountByType(this);
    }
}

