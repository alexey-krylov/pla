package com.pla.grouplife.claim.presentation.dto;

import com.pla.grouplife.sharedresource.model.vo.CoveragePremiumDetail;
import com.pla.grouplife.sharedresource.model.vo.Insured;
import com.pla.grouplife.sharedresource.model.vo.InsuredDependent;
import com.pla.grouplife.sharedresource.model.vo.PlanPremiumDetail;
import com.pla.sharedkernel.domain.model.FamilyId;
import com.pla.sharedkernel.domain.model.Gender;
import com.pla.sharedkernel.domain.model.Relationship;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.util.Set;

/**
 * Created by Admin on 9/21/2015.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AssuredDetailDto {

    private String companyName;

    private String manNumber;

    private String nrcNumber;

    private String salutation;

    private String firstName;

    private String lastName;

    private LocalDate dateOfBirth;

    private Gender gender;

    private String category;

    private BigDecimal annualIncome;

    private String occupationClass;

    private String occupationCategory;

    private Integer noOfAssured;

    private PlanPremiumDetail planPremiumDetail;

    private Set<CoveragePremiumDetail> coveragePremiumDetails;

    private FamilyId familyId;

    private Relationship relationship;

    public static AssuredDetailDto getInstance(Insured insured){
        AssuredDetailDtoBuilder assuredDetailDtoBuilder =new AssuredDetailDtoBuilder();
        assuredDetailDtoBuilder.withFirstName(insured.getFirstName())
                .withLastName(insured.getLastName())
                .withDateOfBirth(insured.getDateOfBirth())
                .withGender(insured.getGender())
                .withCategory(insured.getCategory())
                .withAnnualIncome(insured.getAnnualIncome())
                .withOccupationClass(insured.getOccupationClass())
                .withOccupationCategory(insured.getOccupationCategory())
                .withNoOfAssured(insured.getNoOfAssured())
                .withPlanPremiumDetail(insured.getPlanPremiumDetail())
                .withCoveragePremiumDetails(insured.getCoveragePremiumDetails())
                .withFamilyId(insured.getFamilyId())
                .withRelationship(insured.getRelationship());
        return assuredDetailDtoBuilder.createAssuredDetailDto();
    }

    public static AssuredDetailDto getInstance(InsuredDependent insuredDependent){
        AssuredDetailDtoBuilder assuredDetailDtoBuilder = new AssuredDetailDtoBuilder();
        assuredDetailDtoBuilder.withFirstName(insuredDependent.getFirstName())
                .withLastName(insuredDependent.getLastName())
                .withDateOfBirth(insuredDependent.getDateOfBirth())
                .withGender(insuredDependent.getGender())
                .withCategory(insuredDependent.getCategory())
                .withOccupationClass(insuredDependent.getOccupationClass())
                .withOccupationCategory(insuredDependent.getOccupationCategory())
                .withNoOfAssured(insuredDependent.getNoOfAssured())
                .withPlanPremiumDetail(insuredDependent.getPlanPremiumDetail())
                .withCoveragePremiumDetails(insuredDependent.getCoveragePremiumDetails())
                .withFamilyId(insuredDependent.getFamilyId())
                .withRelationship(insuredDependent.getRelationship());
        return assuredDetailDtoBuilder.createAssuredDetailDto();
    }

}
