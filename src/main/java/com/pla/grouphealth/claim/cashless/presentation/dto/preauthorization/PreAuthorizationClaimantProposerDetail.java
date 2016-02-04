package com.pla.grouphealth.claim.cashless.presentation.dto.preauthorization;

import com.pla.grouphealth.sharedresource.model.vo.GHProposer;
import com.pla.grouphealth.sharedresource.model.vo.GHProposerContactDetail;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static org.nthdimenzion.utils.UtilValidator.*;

@Getter
@Setter
@NoArgsConstructor
public class PreAuthorizationClaimantProposerDetail {
    String proposerName;
    String proposerCode;
    String address1;
    String address2;
    String postalCode;
    String province;
    String town;
    String emailId;
    String workPhone;
    String contactPersonName;
    String contactPersonWorkPhone;
    String contactPersonMobileNumber;
    String contactPersonEmailId;

    public PreAuthorizationClaimantProposerDetail updateWithProposerDetails(GHProposer proposer) {
        this.proposerName = proposer.getProposerName();
        this.proposerCode = proposer.getProposerCode();
        GHProposerContactDetail contactDetail = proposer.getContactDetail();
        if(isNotEmpty(contactDetail)){
            this.address1 = contactDetail.getAddressLine1();
            this.address2 = contactDetail.getAddressLine2();
            this.postalCode = contactDetail.getPostalCode();
            this.province = contactDetail.getProvince();
            this.town = contactDetail.getTown();
            this.emailId = contactDetail.getEmailAddress();
            GHProposerContactDetail.ContactPersonDetail contactPersonDetail = contactDetail.getContactPersonDetail().iterator().next();
            if(isNotEmpty(contactPersonDetail)) {
                this.contactPersonName = contactPersonDetail.getContactPersonName();
                this.contactPersonWorkPhone = contactPersonDetail.getWorkPhoneNumber();
                this.contactPersonMobileNumber = contactPersonDetail.getMobileNumber();
                this.contactPersonEmailId = contactPersonDetail.getContactPersonEmail();
            }
        }
        return this;
    }
}