package com.pla.grouphealth.sharedresource.model.vo;

import lombok.Getter;

import static com.google.common.base.Preconditions.checkArgument;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Samir on 4/8/2015.
 */
@Getter
public class GHProposerBuilder {

    private String proposerName;

    private String proposerCode;

    private GHProposerContactDetail proposerContactDetail;


    GHProposerBuilder(String proposerName, String proposerCode) {
        checkArgument(isNotEmpty(proposerName));
        checkArgument(isNotEmpty(proposerCode));
        this.proposerName = proposerName;
        this.proposerCode = proposerCode;
    }

    GHProposerBuilder(String proposerName) {
        checkArgument(isNotEmpty(proposerName));
        this.proposerName = proposerName;
    }


    public GHProposerBuilder withContactDetail(String addressLine1, String addressLine2, String postalCode, String province, String town, String emailAddress) {
        this.proposerContactDetail = new GHProposerContactDetail(addressLine1, addressLine2, postalCode, province, town, emailAddress);
        return this;
    }

    public GHProposerBuilder withContactPersonDetail(String contactPersonName, String contactPersonEmail, String mobileNumber, String workPhoneNumber) {
        checkArgument(proposerContactDetail != null);
        this.proposerContactDetail = this.proposerContactDetail.addContactPersonDetail(contactPersonName, contactPersonEmail, mobileNumber, workPhoneNumber);
        return this;
    }

    public GHProposer build() {
        return new GHProposer(this);
    }

}
