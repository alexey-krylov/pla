package com.pla.grouphealth.sharedresource.model.vo;

import com.google.common.collect.Lists;
import com.pla.grouphealth.claim.cashless.presentation.dto.PreAuthorizationClaimantProposerDetail;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Samir on 4/7/2015.
 */
@ValueObject
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
@Setter(value = AccessLevel.PACKAGE)
public class GHProposerContactDetail {

    private List<ContactPersonDetail> contactPersonDetail;

    private String addressLine1;

    private String addressLine2;

    private String postalCode;

    private String province;

    private String town;

    private String emailAddress;


    GHProposerContactDetail(String addressLine1, String addressLine2, String postalCode, String province, String town, String emailAddress) {
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.postalCode = postalCode;
        this.province = province;
        this.town = town;
        this.emailAddress = emailAddress;
    }

    public GHProposerContactDetail addContactPersonDetail(List<ContactPersonDetail> contactPersonDetail) {
        this.contactPersonDetail = contactPersonDetail;
        return this;
    }

    public GHProposerContactDetail updateWithContactDetails(PreAuthorizationClaimantProposerDetail preAuthorizationClaimantProposerDetail) {
        this.addressLine1 = preAuthorizationClaimantProposerDetail.getAddress1();
        this.addressLine2 = preAuthorizationClaimantProposerDetail.getAddress2();
        this.postalCode = preAuthorizationClaimantProposerDetail.getPostalCode();
        this.province = preAuthorizationClaimantProposerDetail.getProvince();
        this.town = preAuthorizationClaimantProposerDetail.getTown();
        this.emailAddress = preAuthorizationClaimantProposerDetail.getEmailId();
        this.contactPersonDetail = constructContactPersonDetail(preAuthorizationClaimantProposerDetail);
        return this;
    }

    private List<ContactPersonDetail> constructContactPersonDetail(PreAuthorizationClaimantProposerDetail preAuthorizationClaimantProposerDetail) {
        List<ContactPersonDetail> personDetails = isNotEmpty(this.contactPersonDetail) ? this.contactPersonDetail : Lists.newArrayList();
        if(isNotEmpty(personDetails)){
            return personDetails.parallelStream().map(contactPersonDetail -> contactPersonDetail.updateWithContactPersonDetail(preAuthorizationClaimantProposerDetail)).collect(Collectors.toList());
        }
        ContactPersonDetail contactPersonDetail = new ContactPersonDetail(preAuthorizationClaimantProposerDetail.getContactPersonEmailId(), preAuthorizationClaimantProposerDetail.getContactPersonName(), preAuthorizationClaimantProposerDetail.getContactPersonMobileNumber(), preAuthorizationClaimantProposerDetail.getContactPersonWorkPhone());
        return Lists.newArrayList(contactPersonDetail);
    }

    @Getter
    public class ContactPersonDetail {

        private String contactPersonName;

        private String contactPersonEmail;

        private String mobileNumber;

        private String workPhoneNumber;

        ContactPersonDetail(String contactPersonEmail, String contactPersonName, String mobileNumber, String workPhoneNumber) {
            this.contactPersonEmail = contactPersonEmail;
            this.contactPersonName = contactPersonName;
            this.mobileNumber = mobileNumber;
            this.workPhoneNumber = workPhoneNumber;
        }

        public ContactPersonDetail updateWithContactPersonDetail(PreAuthorizationClaimantProposerDetail preAuthorizationClaimantProposerDetail) {
            return new ContactPersonDetail(preAuthorizationClaimantProposerDetail.getContactPersonEmailId(), preAuthorizationClaimantProposerDetail.getContactPersonName(), preAuthorizationClaimantProposerDetail.getContactPersonMobileNumber(), preAuthorizationClaimantProposerDetail.getContactPersonWorkPhone());
        }
    }


    public String getAddress(String townName, String provinceName) {
        String addressLine2 = isNotEmpty(this.addressLine2) ? "," + this.addressLine2 : "";
        String postalCode = isNotEmpty(this.postalCode) ? "," + this.postalCode : "";
        return addressLine1 + addressLine2 + postalCode + "," + townName + "," + provinceName;
    }
}
