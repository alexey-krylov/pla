package com.pla.individuallife.endorsement.presentation.dto;

import com.pla.individuallife.endorsement.domain.model.ILEndorsement;
import com.pla.individuallife.endorsement.domain.model.IndividualLifeEndorsement;
import com.pla.individuallife.policy.presentation.dto.ILPolicyDto;
import com.pla.individuallife.sharedresource.model.ILEndorsementType;
import com.pla.individuallife.sharedresource.model.vo.ILProposerDocument;
import com.pla.individuallife.sharedresource.model.vo.PremiumDetail;
import com.pla.sharedkernel.domain.model.EndorsementStatus;
import com.pla.sharedkernel.domain.model.Policy;
import com.pla.sharedkernel.identifier.EndorsementId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.DateTime;

import java.util.Set;

import static org.nthdimenzion.presentation.AppUtils.getIntervalInDays;

/**
 * Created by Raghu Bandi on 8/27/2015.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ILEndorsementDto {

    private EndorsementId endorsementId;

    private String endorsementNumber;

    private String endorsementRequestNumber;

    private String policyNumber;

    private String endorsementType;

    private String endorsementCode;

    private DateTime effectiveDate;

    private String policyHolderName;

    private Integer aging;

    private String endorsementStatus;

    // The following additional field have been added by Raghu Bandi

    private EndorsementStatus status;

    private ILEndorsement endorsement;

    private Policy policy;

    private Set<ILProposerDocument> proposerDocuments;

    private DateTime submittedOn;

    private PremiumDetail premiumDetail;

    private ILPolicyDto ilPolicyDto;

    public ILEndorsementDto(EndorsementId endorsementId,String endorsementNumber,String endorsementRequestNumber, String policyNumber, String endorsementTypeInString,
                            String endorsementCode, DateTime effectiveDate, String policyHolderName, Integer age, String endorsementStatus) {

        this.endorsementId = endorsementId;
        this.endorsementNumber = endorsementNumber;
        this.endorsementRequestNumber = endorsementRequestNumber;
        this.policyNumber = policyNumber;
        this.endorsementType = endorsementTypeInString;
        this.endorsementCode = endorsementCode;
        this.effectiveDate = effectiveDate;
        this.policyHolderName = policyHolderName;
        this.aging = age;
        this.endorsementStatus = endorsementStatus;
    }
    public ILEndorsementDto (IndividualLifeEndorsement endorsement) {
        endorsementId = endorsement.getEndorsementId();
        endorsementNumber = endorsement.getEndorsementNumber();
        endorsementRequestNumber = endorsement.getEndorsementRequestNumber();
        policyNumber = endorsement.getIlPolicyDto().getPolicyNumber().getPolicyNumber();
        endorsementType = endorsement.getEndorsementType().getDescription();
        effectiveDate = endorsement.getEffectiveDate();
        policyHolderName = endorsement.getIlPolicyDto().getPolicyHolder().getFirstName();
         // The following additional field have been added by Raghu Bandi
        this.proposerDocuments = endorsement.getProposerDocuments();
        this.status = endorsement.getStatus();
        this.endorsement = endorsement.getEndorsement();
        policy = endorsement.getPolicy();
        this.submittedOn = endorsement.getSubmittedOn();
        premiumDetail = endorsement.getPremiumDetail();
        ilPolicyDto = endorsement.getIlPolicyDto();

    }
}
