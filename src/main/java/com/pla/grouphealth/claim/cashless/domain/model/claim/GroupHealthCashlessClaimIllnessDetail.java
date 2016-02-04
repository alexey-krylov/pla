package com.pla.grouphealth.claim.cashless.domain.model.claim;

import com.pla.grouphealth.claim.cashless.presentation.dto.ClaimUploadedExcelDataDto;
import lombok.*;
import org.hibernate.annotations.Immutable;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;
import org.nthdimenzion.utils.UtilValidator;

import javax.persistence.Embeddable;

/**
 * Author - Mohan Sharma Created on 1/9/2016.
 */
@ValueObject
@Immutable
@Embeddable
@EqualsAndHashCode
@Getter
@Setter
public class GroupHealthCashlessClaimIllnessDetail {
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

    public GroupHealthCashlessClaimIllnessDetail updateWithDetails(ClaimUploadedExcelDataDto claimUploadedExcelDataDto) {
        if(UtilValidator.isNotEmpty(claimUploadedExcelDataDto)) {
            this.HTN = claimUploadedExcelDataDto.getPastHistorySufferingFromHTN();
            this.HTNDetails = claimUploadedExcelDataDto.getDetailsOfHTN();
            this.idhHOD = claimUploadedExcelDataDto.getPastHistorySufferingFromIHCCAD();
            this.IHDHODDetails = claimUploadedExcelDataDto.getDetailsOfIHDCAD();
            this.diabetes = claimUploadedExcelDataDto.getPastHistorySufferingFromDiabetes();
            this.diabetesDetails = claimUploadedExcelDataDto.getDetailsOfDiabetes();
            this.asthmaCOPDTB = claimUploadedExcelDataDto.getPastHistorySufferingFromAsthmaCOPDTB();
            this.asthmaCOPDTBDetails = claimUploadedExcelDataDto.getDetailsOfAsthmaCOPDTB();
            this.STDHIVAIDS = claimUploadedExcelDataDto.getPastHistorySufferingFromStdHivAids();
            this.STDHIVAIDSDetails = claimUploadedExcelDataDto.getDetailOfStdHivAids();
            this.arthritis = claimUploadedExcelDataDto.getPastHistorySufferingFromArthritis();
            this.arthritisDetails = claimUploadedExcelDataDto.getDetailsOfSufferingFromArthritis();
            this.cancerTumorCyst = claimUploadedExcelDataDto.getPastHistorySufferingFromCancerTumorCyst();
            this.cancerTumorCystDetails = claimUploadedExcelDataDto.getDetailsOfCancerTumorCyst();
            this.alcoholDrugAbuse = claimUploadedExcelDataDto.getPastHistorySufferingFromAlcoholDrugAbuse();
            this.alcoholDrugAbuseDetails = claimUploadedExcelDataDto.getDetailOfAlcoholDrugAbuse();
            this.psychiatricCondition = claimUploadedExcelDataDto.getPastHistorySufferingFromPsychiatricCondition();
            this.psychiatricConditionDetails = claimUploadedExcelDataDto.getDetailsPsychiatricCondition();
        }
        return this;
    }
}
