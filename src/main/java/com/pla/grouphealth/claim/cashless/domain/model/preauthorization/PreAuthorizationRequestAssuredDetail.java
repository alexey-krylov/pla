package com.pla.grouphealth.claim.cashless.domain.model.preauthorization;

import com.pla.grouphealth.claim.cashless.presentation.dto.AssuredDetail;
import com.pla.grouphealth.claim.cashless.presentation.dto.ClaimantPolicyDetailDto;
import com.pla.grouphealth.claim.cashless.presentation.dto.DependentAssuredDetail;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.LocalDate;

import java.math.BigDecimal;

import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Author - Mohan Sharma Created on 1/11/2016.
 */
@Getter
@Setter
@NoArgsConstructor
public class PreAuthorizationRequestAssuredDetail {
    private String salutation;
    private String firstName;
    private String surname;
    private LocalDate dateOfBirth;
    private int ageNextBirthday;
    private String nrcNumber;
    private String gender;
    private BigDecimal sumAssured;
    private BigDecimal reserveAmount;
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

    public PreAuthorizationRequestAssuredDetail updateWithAssuredDetails(ClaimantPolicyDetailDto claimantPolicyDetailDto) {
        AssuredDetail assuredDetail = claimantPolicyDetailDto.getAssuredDetail();
        DependentAssuredDetail dependentAssuredDetail = claimantPolicyDetailDto.getDependentAssuredDetail();
        if(isNotEmpty(assuredDetail)) {
            this.salutation = assuredDetail.getSalutation();
            this.firstName = assuredDetail.getFirstName();
            this.surname = assuredDetail.getSurname();
            this.dateOfBirth = assuredDetail.getDateOfBirth();
            this.ageNextBirthday = assuredDetail.getAgeNextBirthday();
            this.nrcNumber = assuredDetail.getNrcNumber();
            this.gender = assuredDetail.getGender();
            this.sumAssured = assuredDetail.getSumAssured();
            this.reserveAmount = assuredDetail.getReserveAmount();
            this.category = assuredDetail.getCategory();
            this.manNumber = assuredDetail.getManNumber();
            this.clientId = assuredDetail.getClientId();
            this.dependentAssuredDetailPresent = Boolean.FALSE;
        } else{
            this.salutation = dependentAssuredDetail.getSalutation();
            this.firstName = dependentAssuredDetail.getFirstName();
            this.surname = dependentAssuredDetail.getSurname();
            this.dateOfBirth = dependentAssuredDetail.getDateOfBirth();
            this.ageNextBirthday = dependentAssuredDetail.getAgeNextBirthday();
            this.nrcNumber = dependentAssuredDetail.getNrcNumber();
            this.gender = dependentAssuredDetail.getGender();
            this.sumAssured = dependentAssuredDetail.getSumAssured();
            this.reserveAmount = dependentAssuredDetail.getReserveAmount();
            this.category = dependentAssuredDetail.getCategory();
            this.manNumber = dependentAssuredDetail.getManNumber();
            this.clientId = dependentAssuredDetail.getClientId();
            this.mainAssuredClientId = dependentAssuredDetail.getMainAssuredClientId();
            this.mainAssuredFullName = dependentAssuredDetail.getMainAssuredFullName();
            this.relationshipWithMainAssured = dependentAssuredDetail.getRelationshipWithMainAssured();
            this.mainAssuredNRC = dependentAssuredDetail.getMainAssuredNRC();
            this.mainAssuredMANNumber = dependentAssuredDetail.getMainAssuredMANNumber();
            this.mainAssuredLastSalary = dependentAssuredDetail.getMainAssuredLastSalary();
            this.dependentAssuredDetailPresent = Boolean.TRUE;
        }
        return this;
    }
}
