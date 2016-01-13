package com.pla.grouphealth.sharedresource.dto;

import com.google.common.base.Objects;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Mohan Sharma on 11/20/2015.
 */
@Getter
@Setter
@EqualsAndHashCode(of = {"relationship","planCode","planSumAssured"})
@NoArgsConstructor
public class RelationshipCoverDetail {
    private String relationship;
    private String planCode;
    private BigDecimal planSumAssured;
    private Integer incomeMultiplier;
    private List<Cover> cover;

    @EqualsAndHashCode(of = {"coverageCode","coverageSumAssured","premiumType"})
    @Getter
    private class Cover {
        private String coverageCode;
        private BigDecimal coverageSumAssured;
        private String premiumType;
    }

    public RelationshipCoverDetail withRelationship(String relationship){
        this.relationship = relationship;
        return this;
    }

    public RelationshipCoverDetail withRelationDetail(String relationship,String planCode,BigDecimal planSumAssured){
        this.relationship = relationship;
        this.planCode = planCode;
        this.planSumAssured = planSumAssured;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RelationshipCoverDetail that = (RelationshipCoverDetail) o;
        if (Objects.equal(relationship, that.relationship)) {
            return (
                    Objects.equal(relationship, that.relationship) &&
                            Objects.equal(planCode, that.planCode) &&
                            Objects.equal(planSumAssured, that.planSumAssured)
            );
        }
        return false;
    }

}
