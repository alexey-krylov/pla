package com.pla.grouphealth.claim.cashless.presentation.dto;

import com.pla.grouphealth.claim.cashless.domain.model.PreAuthorizationDetail;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Mohan Sharma on 1/7/2016.
 */
@Getter
@Setter
public class DrugServiceDto {
    private String type;
    private String serviceName;
    private String drugName;
    private String drugType;
    private String accommodationType;
    private String duration;
    private int lengthOfStay;
    private String strength;

    public DrugServiceDto updateWithDetails(PreAuthorizationDetail preAuthorizationDetail) {
        this.type = preAuthorizationDetail.getType();
        this.serviceName = preAuthorizationDetail.getService();
        this.drugName = preAuthorizationDetail.getDiagnosisTreatmentDrugName();
        this.drugType = preAuthorizationDetail.getDiagnosisTreatmentDrugType();
        this.accommodationType = preAuthorizationDetail.getDiagnosisTreatmentSurgeryAccommodationType();
        this.duration = preAuthorizationDetail.getDiagnosisTreatmentMedicalDuration();
        //this.lengthOfStay = preAuthorizationDetail.sty
        this.strength = preAuthorizationDetail.getDiagnosisTreatmentDrugStrength();
        return this;
    }
}
