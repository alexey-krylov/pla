package com.pla.grouphealth.claim.cashless.presentation.dto.claim;

import com.pla.grouphealth.claim.cashless.domain.model.claim.GroupHealthCashlessClaimDiagnosisTreatmentDetail;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.beanutils.BeanUtils;
import org.joda.time.LocalDate;
import org.nthdimenzion.utils.UtilValidator;

import java.lang.reflect.InvocationTargetException;

import static org.nthdimenzion.utils.UtilValidator.*;

/**
 * Author - Mohan Sharma Created on 2/4/2016.
 */
@Getter
@Setter
@NoArgsConstructor
public class GroupHealthCashlessClaimDiagnosisTreatmentDetailDto {
    private String doctorName;
    private String doctorContactNumber;
    private String indicateWhether;
    private String pregnancyG;
    private String pregnancyP;
    private String pregnancyL;
    private String pregnancyA;
    private LocalDate pregnancyDateOfDelivery;
    private String modeOdDelivery;
    private String nameOfIllnessDisease;
    private String relevantClinicalFinding;
    private String durationOfPresentAilment;
    private LocalDate dateOfConsultation;
    private String pastHistoryOfPresentAilment;
    private String provisionalDiagnosis;
    private String lineOfTreatment;
    private String indicateTest;
    private String nameOfSurgery;
    private LocalDate dateOfAdmission;
    private int lengthOfStay;
    private String typeOfAccommodation;

    public GroupHealthCashlessClaimDiagnosisTreatmentDetailDto updateWithDetails(GroupHealthCashlessClaimDiagnosisTreatmentDetail groupHealthCashlessClaimDiagnosisTreatmentDetail) {
        if(isNotEmpty(groupHealthCashlessClaimDiagnosisTreatmentDetail)){
            try {
                BeanUtils.copyProperties(this, groupHealthCashlessClaimDiagnosisTreatmentDetail);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return this;
    }
}
