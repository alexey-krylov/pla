package com.pla.grouphealth.sharedresource.dto;

import com.pla.grouphealth.sharedresource.model.vo.GHProposer;
import com.pla.grouphealth.sharedresource.model.vo.GHProposerContactDetail;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Samir on 4/9/2015.
 */
@Getter
@Setter
@NoArgsConstructor
public class ProposerDto {

    private String proposerName;

    private String proposerCode;

    private String addressLine1;

    private String addressLine2;

    private String postalCode;

    private String province;

    private String town;

    private String emailAddress;

    private String contactPersonName;

    private String contactPersonEmail;

    private String contactPersonMobileNumber;

    private String contactPersonWorkPhoneNumber;

    private String opportunityId;

    private boolean considerMoratoriumPeriod;

    private boolean samePlanForAllRelation;

    private boolean samePlanForAllCategory;

    public ProposerDto(GHProposer proposer) {
        GHProposerContactDetail proposerContactDetail = proposer.getContactDetail();
        GHProposerContactDetail.ContactPersonDetail contactPersonDetail = proposerContactDetail != null ? proposerContactDetail.getContactPersonDetail() : null;
        this.proposerName = proposer.getProposerName();
        this.proposerCode = proposer.getProposerCode();
        this.addressLine1 = proposerContactDetail != null ? proposerContactDetail.getAddressLine1() : "";
        this.addressLine2 = proposerContactDetail != null ? proposerContactDetail.getAddressLine2() : "";
        this.postalCode = proposerContactDetail != null ? proposerContactDetail.getPostalCode() : "";
        this.province = proposerContactDetail != null ? proposerContactDetail.getProvince() : "";
        this.town = proposerContactDetail != null ? proposerContactDetail.getTown() : "";
        this.emailAddress = proposerContactDetail != null ? proposerContactDetail.getEmailAddress() : "";
        this.contactPersonName = contactPersonDetail != null ? contactPersonDetail.getContactPersonName() : "";
        this.contactPersonEmail = contactPersonDetail != null ? contactPersonDetail.getContactPersonEmail() : "";
        this.contactPersonMobileNumber = contactPersonDetail != null ? contactPersonDetail.getMobileNumber() : "";
        this.contactPersonWorkPhoneNumber = contactPersonDetail != null ? contactPersonDetail.getWorkPhoneNumber() : "";
    }
}
