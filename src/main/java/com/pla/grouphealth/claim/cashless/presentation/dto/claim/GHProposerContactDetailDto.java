package com.pla.grouphealth.claim.cashless.presentation.dto.claim;

import com.google.common.collect.Lists;
import com.pla.grouphealth.sharedresource.dto.ContactPersonDetailDto;
import com.pla.grouphealth.sharedresource.model.vo.GHProposerContactDetail;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.beanutils.BeanUtils;
import org.nthdimenzion.utils.UtilValidator;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Author - Mohan Sharma Created on 2/8/2016.
 */
@Getter
@Setter
@NoArgsConstructor
public class GHProposerContactDetailDto {

    private List<ContactPersonDetailDto> contactPersonDetail;

    private String addressLine1;

    private String addressLine2;

    private String postalCode;

    private String province;

    private String town;

    private String emailAddress;

    public GHProposerContactDetailDto updateWithContacts(GHProposerContactDetail contactDetail) {
        if(UtilValidator.isNotEmpty(contactDetail)){
            this.addressLine1 = contactDetail.getAddressLine1();
            this.addressLine2 = contactDetail.getAddressLine2();
            this.postalCode = contactDetail.getPostalCode();
            this.province = contactDetail.getProvince();
            this.town = contactDetail.getTown();
            this.emailAddress = contactDetail.getEmailAddress();
            this.contactPersonDetail = constructContactPersonDetail(contactDetail.getContactPersonDetail());
        }
        return this;
    }

    private List<ContactPersonDetailDto> constructContactPersonDetail(List<GHProposerContactDetail.ContactPersonDetail> contactPersonDetail) {
        return UtilValidator.isNotEmpty(contactPersonDetail) ? contactPersonDetail.stream().map(new Function<GHProposerContactDetail.ContactPersonDetail, ContactPersonDetailDto>() {
            @Override
            public ContactPersonDetailDto apply(GHProposerContactDetail.ContactPersonDetail contactPersonDetail) {
                return new ContactPersonDetailDto().updateWithDetails(contactPersonDetail);
            }
        }).collect(Collectors.toList()) : Lists.newArrayList();
    }
}
