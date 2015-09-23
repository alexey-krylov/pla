package com.pla.grouplife.claim.presentation.dto;

import com.pla.grouplife.sharedresource.model.vo.CoveragePremiumDetail;
import com.pla.grouplife.sharedresource.model.vo.PlanPremiumDetail;
import com.pla.sharedkernel.domain.model.FamilyId;
import com.pla.sharedkernel.domain.model.Gender;
import com.pla.sharedkernel.domain.model.Relationship;
import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.util.Set;

public class AssuredDetailDtoBuilder {
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

    public AssuredDetailDtoBuilder withCompanyName(String companyName) {
        this.companyName = companyName;
        return this;
    }

    public AssuredDetailDtoBuilder withManNumber(String manNumber) {
        this.manNumber = manNumber;
        return this;
    }

    public AssuredDetailDtoBuilder withNrcNumber(String nrcNumber) {
        this.nrcNumber = nrcNumber;
        return this;
    }

    public AssuredDetailDtoBuilder withSalutation(String salutation) {
        this.salutation = salutation;
        return this;
    }

    public AssuredDetailDtoBuilder withFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public AssuredDetailDtoBuilder withLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public AssuredDetailDtoBuilder withDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
        return this;
    }

    public AssuredDetailDtoBuilder withGender(Gender gender) {
        this.gender = gender;
        return this;
    }

    public AssuredDetailDtoBuilder withCategory(String category) {
        this.category = category;
        return this;
    }

    public AssuredDetailDtoBuilder withAnnualIncome(BigDecimal annualIncome) {
        this.annualIncome = annualIncome;
        return this;
    }

    public AssuredDetailDtoBuilder withOccupationClass(String occupationClass) {
        this.occupationClass = occupationClass;
        return this;
    }

    public AssuredDetailDtoBuilder withOccupationCategory(String occupationCategory) {
        this.occupationCategory = occupationCategory;
        return this;
    }

    public AssuredDetailDtoBuilder withNoOfAssured(Integer noOfAssured) {
        this.noOfAssured = noOfAssured;
        return this;
    }

    public AssuredDetailDtoBuilder withPlanPremiumDetail(PlanPremiumDetail planPremiumDetail) {
        this.planPremiumDetail = planPremiumDetail;
        return this;
    }

    public AssuredDetailDtoBuilder withCoveragePremiumDetails(Set<CoveragePremiumDetail> coveragePremiumDetails) {
        this.coveragePremiumDetails = coveragePremiumDetails;
        return this;
    }

    public AssuredDetailDtoBuilder withFamilyId(FamilyId familyId) {
        this.familyId = familyId;
        return this;
    }

    public AssuredDetailDtoBuilder withRelationship(Relationship relationship) {
        this.relationship = relationship;
        return this;
    }

    public AssuredDetailDto createAssuredDetailDto() {
        return new AssuredDetailDto(companyName, manNumber, nrcNumber, salutation, firstName, lastName, dateOfBirth, gender, category, annualIncome, occupationClass, occupationCategory, noOfAssured, planPremiumDetail, coveragePremiumDetails, familyId, relationship);
    }
}