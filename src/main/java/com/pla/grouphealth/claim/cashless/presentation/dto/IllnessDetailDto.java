package com.pla.grouphealth.claim.cashless.presentation.dto;

import com.pla.grouphealth.claim.cashless.domain.model.PreAuthorizationDetail;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Mohan Sharma on 1/7/2016.
 */
@Getter
@Setter
public class IllnessDetailDto {
    private String HTN;
    private String HTNDetails;
    private String idhHOD;
    private String IHDHODDetails;
    private String diabetes;
    private String diabetesDetails;
    private String asthmaCOPDTB;
    private String asthmaCOPDTBDetails;
    private String STDHIVAIDS;
    private String STDHIVAIDSDetails;
    private String arthritis;
    private String arthritisDetails;
    private String cancerTumorCyst;
    private String cancerTumorCystDetails;
    private String alcoholDrugAbuse;
    private String alcoholDrugAbuseDetails;
    private String psychiatricCondition;
    private String psychiatricConditionDetails;

    public IllnessDetailDto updateWithDetails(PreAuthorizationDetail preAuthorizationDetail) {
        this.HTN = preAuthorizationDetail.getPastHistorySufferingFromHTN();
        this.HTNDetails = preAuthorizationDetail.getDetailsOfHTN();
        this.idhHOD = preAuthorizationDetail.getPastHistorySufferingFromIHCCAD();
        this.IHDHODDetails = preAuthorizationDetail.getDetailsOfIHDCAD();
        this.diabetes = preAuthorizationDetail.getPastHistorySufferingFromDiabetes();
        this.diabetesDetails = preAuthorizationDetail.getDetailsOfDiabetes();
        this.asthmaCOPDTB = preAuthorizationDetail.getPastHistorySufferingFromAsthmaCOPDTB();
        this.asthmaCOPDTBDetails = preAuthorizationDetail.getDetailsOfAsthmaCOPDTB();
        this.STDHIVAIDS = preAuthorizationDetail.getPastHistorySufferingFromStdHivAids();
        this.STDHIVAIDSDetails = preAuthorizationDetail.getDetailOfStdHivAids();
        this.arthritis = preAuthorizationDetail.getPastHistorySufferingFromArthritis();
        this.arthritisDetails = preAuthorizationDetail.getDetailsOfSufferingFromArthritis();
        this.cancerTumorCyst = preAuthorizationDetail.getPastHistorySufferingFromCancerTumorCyst();
        this.cancerTumorCystDetails = preAuthorizationDetail.getDetailsOfCancerTumorCyst();
        this.alcoholDrugAbuse = preAuthorizationDetail.getPastHistorySufferingFromAlcoholDrugAbuse();
        this.alcoholDrugAbuseDetails = preAuthorizationDetail.getDetailOfAlcoholDrugAbuse();
        this.psychiatricCondition = preAuthorizationDetail.getPastHistorySufferingFromPsychiatricCondition();
        this.psychiatricConditionDetails = preAuthorizationDetail.getDetailsPsychiatricCondition();
        return this;
    }
}
