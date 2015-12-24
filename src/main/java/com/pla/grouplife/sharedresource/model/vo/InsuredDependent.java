package com.pla.grouplife.sharedresource.model.vo;

import com.pla.publishedlanguage.domain.model.ComputedPremiumDto;
import com.pla.sharedkernel.domain.model.FamilyId;
import com.pla.sharedkernel.domain.model.Gender;
import com.pla.sharedkernel.domain.model.Relationship;
import com.pla.sharedkernel.identifier.PlanId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.LocalDate;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Samir on 4/7/2015.
 */
@ValueObject
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
@Setter(value = AccessLevel.PACKAGE)
public class InsuredDependent {

    private String companyName;

    private String manNumber;

    private String nrcNumber;

    private String salutation;

    private String firstName;

    private String lastName;

    private LocalDate dateOfBirth;

    private Gender gender;

    private String category;

    private Relationship relationship;

    private String occupationClass;

    private String occupationCategory;

    private PlanPremiumDetail planPremiumDetail;

    private Set<CoveragePremiumDetail> coveragePremiumDetails;

    private Integer noOfAssured;

    private FamilyId familyId;

    private Boolean isDependentDeleted  = Boolean.FALSE;

    InsuredDependent(InsuredDependentBuilder insuredDependentBuilder) {
        checkArgument(insuredDependentBuilder != null);
        this.companyName = insuredDependentBuilder.getCompanyName();
        this.manNumber = insuredDependentBuilder.getManNumber();
        this.salutation = insuredDependentBuilder.getSalutation();
        this.nrcNumber = insuredDependentBuilder.getNrcNumber();
        this.firstName = insuredDependentBuilder.getFirstName();
        this.lastName = insuredDependentBuilder.getLastName();
        this.dateOfBirth = insuredDependentBuilder.getDateOfBirth();
        this.gender = insuredDependentBuilder.getGender();
        this.category = insuredDependentBuilder.getCategory();
        this.relationship = insuredDependentBuilder.getRelationship();
        this.planPremiumDetail = insuredDependentBuilder.getPlanPremiumDetail();
        this.coveragePremiumDetails = insuredDependentBuilder.getCoveragePremiumDetails();
        this.occupationClass = insuredDependentBuilder.getOccupationClass();
        this.noOfAssured = insuredDependentBuilder.getNoOfAssured();
        if (isNotEmpty(insuredDependentBuilder.getFamilyId())) {
            this.familyId = new FamilyId(insuredDependentBuilder.getFamilyId());
        }
    }

    public static InsuredDependentBuilder getInsuredDependentBuilder(PlanId planId, String planCode, BigDecimal premiumAmount,List<ComputedPremiumDto> computedPremiumDtos, BigDecimal sumAssured) {
        return new InsuredDependentBuilder(planId, planCode, premiumAmount,computedPremiumDtos, sumAssured);
    }

    public InsuredDependent updatePlanPremiumAmount(BigDecimal insuredPlanProratePremium) {
        this.planPremiumDetail = this.planPremiumDetail.updatePremiumAmount(insuredPlanProratePremium);
        return this;
    }

    public InsuredDependent updateWithFamilyId(FamilyId familyId) {
        this.familyId = familyId;
        return this;
    }

    public InsuredDependent updateWithDeletedMembers(List<String> deletedFamilyIds) {
        if (deletedFamilyIds.contains(this.familyId!=null?this.familyId.getFamilyId():"")){
            this.isDependentDeleted = Boolean.TRUE;
        }
        return this;
    }
}
