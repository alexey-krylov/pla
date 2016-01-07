package com.pla.grouplife.sharedresource.model.vo;

import com.pla.sharedkernel.domain.model.FamilyId;
import com.pla.sharedkernel.domain.model.Gender;
import com.pla.sharedkernel.domain.model.PremiumType;
import com.pla.sharedkernel.domain.model.Relationship;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.joda.time.LocalDate;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import java.math.BigDecimal;
import java.util.Set;

/**
 * Created by Admin on 07-Jan-16.
 */
@ValueObject
@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class GLEndorsementInsured extends Insured{

    /*
    * As Insured is used in all over the place and FCL Endorsement has its own behaviors so made the subclass of Insured
    *
    * FCL specific properties will include here without altering the Insured....
    * * */

    private BigDecimal freeCoverLimit;

    public static GLEndorsementInsured create(){
        return new GLEndorsementInsured();
    }

    public GLEndorsementInsured buildGLEndorsementInsured(Insured insured,BigDecimal freeCoverLimit){
        return this.withCompanyName(insured.getCompanyName()).withManNumber(insured.getManNumber())
                .withNRCNumber(insured.getNrcNumber())
                .withPolicyHolderDetail(insured.getSalutation(),insured.getFirstName(),insured.getLastName(),insured.getDateOfBirth(),insured.getGender())
                .withCategory(insured.getCategory())
                .withRelationShip(insured.getRelationship()==null?Relationship.SELF:insured.getRelationship())
                .withOccupationCategory(insured.getOccupationCategory())
                .withOccupationClass(insured.getOccupationClass())
                .withAnnualIncome(insured.getAnnualIncome())
                .withNoOfAssured(insured.getNoOfAssured())
                .withFamilyId(insured.getFamilyId())
                .withPremiumType(insured.getPremiumType())
                .withRateOfPremium(insured.getRateOfPremium())
                .withPlanPremiumDetail(insured.getPlanPremiumDetail())
                .withCoverageDetail(insured.getCoveragePremiumDetails())
                .withFreeCoverLimit(freeCoverLimit);
    }


    public GLEndorsementInsured buildGLEndorsementInsuredDependent(InsuredDependent insuredDependent,BigDecimal freeCoverLimit){
        return this.withCompanyName(insuredDependent.getCompanyName()).withManNumber(insuredDependent.getManNumber())
                .withNRCNumber(insuredDependent.getNrcNumber())
                .withPolicyHolderDetail(insuredDependent.getSalutation(),insuredDependent.getFirstName(),insuredDependent.getLastName(),insuredDependent.getDateOfBirth(),insuredDependent.getGender())
                .withCategory(insuredDependent.getCategory())
                .withRelationShip(insuredDependent.getRelationship()==null?Relationship.SELF:insuredDependent.getRelationship())
                .withOccupationCategory(insuredDependent.getOccupationCategory())
                .withOccupationClass(insuredDependent.getOccupationClass())
                .withNoOfAssured(insuredDependent.getNoOfAssured())
                .withFamilyId(insuredDependent.getFamilyId())
                .withPlanPremiumDetail(insuredDependent.getPlanPremiumDetail())
                .withCoverageDetail(insuredDependent.getCoveragePremiumDetails())
                .withFreeCoverLimit(freeCoverLimit);
    }

    public GLEndorsementInsured withCompanyName(String companyName){
        this.setCompanyName(companyName);
        return this;
    }

    public GLEndorsementInsured withManNumber(String manNumber){
        this.setManNumber(manNumber);
        return this;
    }

    public GLEndorsementInsured withNRCNumber(String nrcNumber){
        this.setNrcNumber(nrcNumber);
        return this;
    }

    public GLEndorsementInsured withPolicyHolderDetail(String salutation,String firstName,String lastName,LocalDate dateOfBirth,Gender gender){
        this.setSalutation(salutation);
        this.setFirstName(firstName);
        this.setLastName(lastName);
        this.setDateOfBirth(dateOfBirth);
        this.setGender(gender);
        return this;
    }

    public GLEndorsementInsured withCategory(String category){
        this.setCategory(category);
        return this;
    }

    public GLEndorsementInsured withRelationShip(Relationship relationShip){
        this.setRelationship(relationShip);
        return this;
    }

    public GLEndorsementInsured withAnnualIncome(BigDecimal annualIncome){
        this.setAnnualIncome(annualIncome);
        return this;
    }

    public GLEndorsementInsured withOccupationClass(String occupationClass){
        this.setOccupationClass(occupationClass);
        return this;
    }

    public GLEndorsementInsured withOccupationCategory(String occupationCategory){
        this.setOccupationCategory(occupationCategory);
        return this;
    }

    public GLEndorsementInsured withNoOfAssured(Integer noOfAssured){
        this.setNoOfAssured(noOfAssured);
        return this;
    }

    public GLEndorsementInsured withPremiumType(PremiumType premiumType){
        this.setPremiumType(premiumType);
        return this;
    }
    public GLEndorsementInsured withRateOfPremium(String rateOfPremium){
        this.setRateOfPremium(rateOfPremium);
        return this;
    }


    public GLEndorsementInsured withPlanPremiumDetail(PlanPremiumDetail planPremiumDetail){
        this.setPlanPremiumDetail(planPremiumDetail);
        return this;
    }

    public GLEndorsementInsured withCoverageDetail(Set<CoveragePremiumDetail> coveragePremiumDetails){
        this.setCoveragePremiumDetails(coveragePremiumDetails);
        return this;
    }

    public GLEndorsementInsured withFamilyId(FamilyId familyId){
        this.setFamilyId(familyId);
        return this;
    }

    public GLEndorsementInsured withFreeCoverLimit(BigDecimal freeCoverLimit){
        this.freeCoverLimit = freeCoverLimit;
        return this;
    }
}
