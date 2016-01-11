package com.pla.grouplife.sharedresource.model;

import com.pla.grouplife.sharedresource.model.vo.UnderWriterFactor;
import com.pla.sharedkernel.domain.model.PremiumType;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * Created by Admin on 08-Jan-16.
 */
@Getter
public enum UnderWriterType {

    LOADING("UnderWriting Loading") {
        @Override
        public BigDecimal getUnderWritingAmountByType(UnderWriterFactor underWriterFactor) {
            return PremiumType.RATE.equals(underWriterFactor.getPremiumType())?underWriterFactor.getUnderWritingLoading().divide(new BigDecimal(100)):underWriterFactor.getUnderWritingLoading();
        }
    },DISCOUNT("UnderWriting Discount") {
        @Override
        public BigDecimal getUnderWritingAmountByType(UnderWriterFactor underWriterFactor) {
            return PremiumType.RATE.equals(underWriterFactor.getPremiumType())?underWriterFactor.getUnderWriterDiscount().divide(new BigDecimal(100)):underWriterFactor.getUnderWriterDiscount();
        }
    };

    private String description;

    UnderWriterType(String description) {
        this.description  = description;
    }

    @Override
    public String toString() {
        return description;
    }

    public abstract BigDecimal getUnderWritingAmountByType(UnderWriterFactor underWriterFactor);
}
