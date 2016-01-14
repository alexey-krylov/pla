package com.pla.grouphealth.sharedresource.model.vo;

import com.google.common.base.Objects;
import com.google.common.collect.Sets;
import com.pla.grouphealth.sharedresource.service.GHInsuredExcelParser;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static com.pla.sharedkernel.util.ExcelGeneratorUtil.getCellValue;
import static org.nthdimenzion.utils.UtilValidator.isEmpty;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Admin on 14-Jan-16.
 */
@Getter
@Setter
@EqualsAndHashCode(of = {"relationship","planCode","planSumAssured"})
@NoArgsConstructor
public class GHRelationshipCategoryCoverDetail {
    private String relationship;
    private String category ="";
    private String planCode;
    private BigDecimal planSumAssured;
    private String premiumType;
    private Set<Cover> cover;

    @EqualsAndHashCode(of = {"coverageCode","coverageSumAssured","premiumType"})
    @Getter
    private class Cover {
        private String coverageCode;
        private BigDecimal coverageSumAssured;
        private String premiumType;

        public Cover withDetail(String coverageCode,BigDecimal coverageSumAssured,String premiumType){
            this.coverageCode = coverageCode;
            this.coverageSumAssured = coverageSumAssured;
            this.premiumType = premiumType;
            return this;
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Cover that = (Cover) o;
            return (
                    Objects.equal(coverageCode, that.coverageCode) &&
                            Objects.equal(coverageSumAssured, that.coverageSumAssured) &&
                            Objects.equal(premiumType, that.premiumType)
            );
        }
    }

    public GHRelationshipCategoryCoverDetail withRelationDetail(String relationship,String planCode,BigDecimal planSumAssured,String premiumType){
        this.relationship = relationship;
        this.planCode = planCode;
        this.planSumAssured = planSumAssured;
        this.premiumType = premiumType;
        return this;
    }

    public GHRelationshipCategoryCoverDetail withCoverageDetail(List<GHInsuredExcelParser.OptionalCoverageCellHolder> optionalCoverageCellHolders){
        Set<Cover> covers = Sets.newLinkedHashSet();
        optionalCoverageCellHolders.forEach(optionalCover->{
            Cover cover = new Cover();
            String coverageSA = getCellValue(optionalCover.getOptionalCoverageSACell());
            BigDecimal coverageSumAssured = isNotEmpty(coverageSA)?new BigDecimal(coverageSA):BigDecimal.ZERO;
            cover.withDetail(getCellValue(optionalCover.getOptionalCoverageCell()), coverageSumAssured, "");
            covers.add(cover);
        });
        this.cover = covers;
        return this;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GHRelationshipCategoryCoverDetail that = (GHRelationshipCategoryCoverDetail) o;
        if (Objects.equal(relationship, that.relationship)) {
            return (
                    Objects.equal(relationship, that.relationship) && Objects.equal(category, that.category) &&
                            Objects.equal(planCode, that.planCode) &&
                            Objects.equal(planSumAssured, that.planSumAssured) &&  Objects.equal(premiumType, that.premiumType)
                            && isCoverageDetailEqual(cover, that.cover)
            );
        }
        return false;
    }

    public boolean compare(GHRelationshipCategoryCoverDetail that){
        return (
                Objects.equal(planCode, that.planCode) &&
                        Objects.equal(planSumAssured, that.planSumAssured) &&  Objects.equal(premiumType, that.premiumType)
                        && isCoverageDetailEqual(cover, that.cover)
        );
    }

    private boolean isCoverageDetailEqual(Set<Cover> left,Set<Cover> right){
        if (isEmpty(left) && isEmpty(right))
            return true;
        if (left.size()==right.size()){
            for (Cover cover : left){
                for (Cover rightCoverDetail:right){
                    return cover.equals(rightCoverDetail);
                }
            }
        }
        return false;
    }
}
