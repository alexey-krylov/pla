package com.pla.grouphealth.sharedresource.dto;

import com.google.common.base.Objects;
import lombok.*;

import java.math.BigDecimal;

/**
 * Created by Mohan Sharma on 11/20/2015.
 */
@Getter
@Setter
@EqualsAndHashCode(of = {"category","cover"})
@NoArgsConstructor
public class CategoryCoverDetail {

    private String category;
    private String planCode;
    private BigDecimal planSumAssured;
    private String premiumType;
    private Cover cover;

    public CategoryCoverDetail withCategoryDetail(String category, String planCode, BigDecimal sumAssured) {
        this.category = category;
        this.planCode = planCode;
        this.planSumAssured = sumAssured;
        return this;
    }

    @EqualsAndHashCode(of = {"planCode","planSumAssured","coverageCode","coverageSumAssured"})
    @Getter
    private class Cover{
        private String coverageCode;
        private BigDecimal coverageSumAssured;
        private String premiumType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CategoryCoverDetail that = (CategoryCoverDetail) o;
        if (Objects.equal(category, that.category)) {
            return (
                    Objects.equal(category, that.category) &&
                            Objects.equal(planCode, that.planCode) &&
                            Objects.equal(planSumAssured, that.planSumAssured)
            );
        }
        return false;
    }

    public boolean compare(CategoryCoverDetail categoryCoverDetail){
        return (
                Objects.equal(planCode, categoryCoverDetail.planCode) &&
                        Objects.equal(planSumAssured, categoryCoverDetail.planSumAssured)
        );
    }
}
