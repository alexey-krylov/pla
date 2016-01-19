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
    private Integer minAgeEntry = 0;
    private Integer maxAgeEntry = 0;
    private Set<Cover> cover;

    @EqualsAndHashCode(of = {"coverageCode","coverageSumAssured","premiumType"})
    @Getter
    private class Cover {
        private String coverageCode;
        private BigDecimal coverageSumAssured;
        private String premiumType;
        private Set<BenefitCover> benefitCovers;


        public Cover withDetail(String coverageCode,BigDecimal coverageSumAssured,String premiumType){
            this.coverageCode = coverageCode;
            this.coverageSumAssured = coverageSumAssured;
            this.premiumType = premiumType;
            return this;
        }


        public Cover withBenefitCover(Set<GHInsuredExcelParser.OptionalCoverageBenefitCellHolder> benefitCellHolders){
            Set<Cover.BenefitCover> benefitCovers = Sets.newLinkedHashSet();
            if (isEmpty(benefitCellHolders)) {
                this.benefitCovers = benefitCovers;
                return this;
            }
            benefitCellHolders.forEach(benefits->{
                String benefitCode = getCellValue(benefits.getBenefitCell());
                String benefitLimit = getCellValue(benefits.getBenefitLimitCell());
                Cover.BenefitCover benefitCover = new BenefitCover().withBenefitCover(benefitCode, new BigDecimal(benefitLimit));
                benefitCovers.add(benefitCover);
            });
            this.benefitCovers = benefitCovers;
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
                            Objects.equal(premiumType, that.premiumType) && isBenefitCoverEqual(benefitCovers,that.benefitCovers)
            );
        }

        public boolean isBenefitCoverEqual(Set<BenefitCover> left,Set<BenefitCover> right){
            if (isEmpty(left) && isEmpty(right))
                return true;
            if (left.size()==right.size()){
                for (BenefitCover benefitCover : left){
                    for (BenefitCover rightCoverDetail:right){
                        return benefitCover.equals(rightCoverDetail);
                    }
                }
            }
            return false;
        }


        @Getter
        @Setter
        private class BenefitCover {
            private String benefitCode;
            private BigDecimal benefitLimit;

            public BenefitCover withBenefitCover(String benefitCode,BigDecimal benefitLimit){
                this.benefitCode = benefitCode;
                this.benefitLimit = benefitLimit;
                return this;
            }


            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                BenefitCover that = (BenefitCover) o;
                return (
                        Objects.equal(benefitCode, that.benefitCode) &&
                                Objects.equal(benefitLimit, that.benefitLimit)
                );
            }
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
            cover = cover.withDetail(getCellValue(optionalCover.getOptionalCoverageCell()), coverageSumAssured, "");
            cover = cover.withBenefitCover(optionalCover.getBenefitCellHolders());
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
                            && isCoverageDetailEqual(cover, that.cover) && this.getMaxAgeEntry().compareTo(that.getMaxAgeEntry())==0 &&
                            this.getMinAgeEntry().compareTo(that.getMinAgeEntry())==0
            );
        }
        return false;
    }

    public boolean compare(GHRelationshipCategoryCoverDetail that){
        return (
                Objects.equal(planCode, that.planCode) &&
                        Objects.equal(planSumAssured, that.planSumAssured) &&  Objects.equal(premiumType, that.premiumType)
                        && isCoverageDetailEqual(cover, that.cover) && this.getMaxAgeEntry().compareTo(that.getMaxAgeEntry())==0 &&
                        this.getMinAgeEntry().compareTo(that.getMinAgeEntry())==0
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
