package com.pla.grouphealth.domain.model.quotation;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

/**
 * Created by Karunakar on 4/30/2015.
 */
@ValueObject
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
@Setter(value = AccessLevel.PACKAGE)
public class ProposerContactDetail {

    private ContactPersonDetail contactPersonDetail;

    private String addressLine1;

    private String addressLine2;

    private String postalCode;

    private String province;

    private String town;

    private String emailAddress;


    ProposerContactDetail(String addressLine1, String addressLine2, String postalCode, String province, String town, String emailAddress) {
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.postalCode = postalCode;
        this.province = province;
        this.town = town;
        this.emailAddress = emailAddress;
    }

    public ProposerContactDetail addContactPersonDetail(String contactPersonName, String contactPersonEmail, String mobileNumber, String workPhoneNumber) {
        this.contactPersonDetail = new ContactPersonDetail(contactPersonEmail, contactPersonName, mobileNumber, workPhoneNumber);
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
}
