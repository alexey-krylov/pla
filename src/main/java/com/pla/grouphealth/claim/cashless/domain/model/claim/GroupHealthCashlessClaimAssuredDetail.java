package com.pla.grouphealth.claim.cashless.domain.model.claim;

import com.pla.grouphealth.sharedresource.model.vo.GHInsured;
import com.pla.grouphealth.sharedresource.model.vo.GHInsuredDependent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.nthdimenzion.presentation.AppUtils;

import java.math.BigDecimal;

import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Author - Mohan Sharma Created on 1/11/2016.
 */
@Getter
@Setter
@NoArgsConstructor
public class GroupHealthCashlessClaimAssuredDetail {
    private String salutation;
    private String firstName;
    private String surname;
    private LocalDate dateOfBirth;
    private int ageNextBirthday;
    private String nrcNumber;
    private String gender;
    private String category;
    private String manNumber;
    private String clientId;
    private String mainAssuredFullName;
    private String relationshipWithMainAssured;
    private String mainAssuredNRC;
    private String mainAssuredMANNumber;
    private BigDecimal mainAssuredLastSalary;
    private String mainAssuredClientId;
    private boolean dependentAssuredDetailPresent;

    public GroupHealthCashlessClaimAssuredDetail updateWithAssuredDetails(GHInsured assuredDetail, String clientId) {
        if(isNotEmpty(assuredDetail)) {
            this.salutation = assuredDetail.getSalutation();
            this.firstName = assuredDetail.getFirstName();
            this.surname = assuredDetail.getSalutation();
            this.dateOfBirth = assuredDetail.getDateOfBirth();
            this.ageNextBirthday = AppUtils.getAgeOnNextBirthDate(assuredDetail.getDateOfBirth());
            this.nrcNumber = assuredDetail.getNrcNumber();
            this.gender = isNotEmpty(assuredDetail.getGender()) ? assuredDetail.getGender().name() : StringUtils.EMPTY;
            this.category = assuredDetail.getCategory();
            this.manNumber = assuredDetail.getManNumber();
            this.clientId = clientId;
            this.dependentAssuredDetailPresent = Boolean.FALSE;
        }
        return this;
    }

    public GroupHealthCashlessClaimAssuredDetail updateWithAssuredDetailsForDependent(GHInsuredDependent ghInsuredDependent, GHInsured groupHealthInsured, String clientId) {
        if(isNotEmpty(ghInsuredDependent)) {
            this.salutation = ghInsuredDependent.getSalutation();
            this.firstName = ghInsuredDependent.getFirstName();
            this.surname = ghInsuredDependent.getLastName();
            this.dateOfBirth = ghInsuredDependent.getDateOfBirth();
            this.ageNextBirthday = AppUtils.getAgeOnNextBirthDate(ghInsuredDependent.getDateOfBirth());
            this.nrcNumber = ghInsuredDependent.getNrcNumber();
            this.gender = isNotEmpty(ghInsuredDependent.getGender()) ? ghInsuredDependent.getGender().name() : StringUtils.EMPTY;
            this.category = ghInsuredDependent.getCategory();
            this.manNumber = ghInsuredDependent.getManNumber();
            this.clientId = clientId;
            if(isNotEmpty(groupHealthInsured)) {
                this.mainAssuredClientId = groupHealthInsured.getFamilyId().getFamilyId();
                this.mainAssuredFullName = groupHealthInsured.getFirstName() + " " + groupHealthInsured.getLastName();
                this.relationshipWithMainAssured = ghInsuredDependent.getRelationship().description;
                this.mainAssuredNRC = groupHealthInsured.getNrcNumber();
                this.mainAssuredMANNumber = groupHealthInsured.getManNumber();
            }
            this.dependentAssuredDetailPresent = Boolean.TRUE;
        }
        return this;
    }
}
