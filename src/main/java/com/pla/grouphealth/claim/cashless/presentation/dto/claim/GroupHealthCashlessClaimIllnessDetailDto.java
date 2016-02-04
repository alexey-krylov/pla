package com.pla.grouphealth.claim.cashless.presentation.dto.claim;

import com.pla.grouphealth.claim.cashless.domain.model.claim.GroupHealthCashlessClaimIllnessDetail;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;

/**
 * Author - Mohan Sharma Created on 2/4/2016.
 */
@Getter
@Setter
@NoArgsConstructor
public class GroupHealthCashlessClaimIllnessDetailDto {
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

    public GroupHealthCashlessClaimIllnessDetailDto updateWithDetails(GroupHealthCashlessClaimIllnessDetail groupHealthCashlessClaimIllnessDetail) {
        try {
            BeanUtils.copyProperties(this, groupHealthCashlessClaimIllnessDetail);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return this;
    }
}
