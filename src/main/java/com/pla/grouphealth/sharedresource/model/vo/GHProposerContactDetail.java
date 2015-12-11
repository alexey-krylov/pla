package com.pla.grouphealth.sharedresource.model.vo;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import java.util.List;

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
    }


    public String getAddress(String townName, String provinceName) {
        String addressLine2 = isNotEmpty(this.addressLine2) ? "," + this.addressLine2 : "";
        String postalCode = isNotEmpty(this.postalCode) ? "," + this.postalCode : "";
        return addressLine1 + addressLine2 + postalCode + "," + townName + "," + provinceName;
    }
}
