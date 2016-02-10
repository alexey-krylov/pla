package com.pla.grouphealth.sharedresource.dto;

import com.pla.grouphealth.sharedresource.model.vo.GHProposerContactDetail;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.nthdimenzion.utils.UtilValidator;

import static org.nthdimenzion.utils.UtilValidator.*;

/**
 * Created by Admin on 10-Dec-15.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ContactPersonDetailDto {

    private String contactPersonName;

    private String contactPersonEmail;

    private String contactPersonMobileNumber;

    private String contactPersonWorkPhoneNumber;

    public ContactPersonDetailDto updateWithDetails(GHProposerContactDetail.ContactPersonDetail contactPersonDetail) {
        if(isNotEmpty(contactPersonDetail)) {
            this.contactPersonName = contactPersonDetail.getContactPersonName();
            this.contactPersonEmail = contactPersonDetail.getContactPersonEmail();
            this.contactPersonMobileNumber = contactPersonDetail.getMobileNumber();
            this.contactPersonWorkPhoneNumber = contactPersonDetail.getWorkPhoneNumber();
        }
        return this;
    }
}
