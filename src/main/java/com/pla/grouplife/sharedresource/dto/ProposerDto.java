package com.pla.grouplife.sharedresource.dto;

import com.pla.grouplife.sharedresource.model.vo.Proposer;
import com.pla.grouplife.sharedresource.model.vo.ProposerContactDetail;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    List<ContactPersonDetailDto> contactPersonDetail;

    private String opportunityId;

    private String industryId;

    private boolean samePlanForAllRelation;

    private boolean samePlanForAllCategory;

    private Boolean hasUploaded = Boolean.FALSE;

    public ProposerDto(Proposer proposer) {
        ProposerContactDetail proposerContactDetail = proposer.getContactDetail();
        List<ContactPersonDetailDto> contactPersonDetail = proposerContactDetail != null ? transformContactPersonDetail(proposerContactDetail.getContactPersonDetail()) : null;
        this.proposerName = proposer.getProposerName();
        this.proposerCode = proposer.getProposerCode();
        this.addressLine1 = proposerContactDetail != null ? proposerContactDetail.getAddressLine1() : "";
        this.addressLine2 = proposerContactDetail != null ? proposerContactDetail.getAddressLine2() : "";
        this.postalCode = proposerContactDetail != null ? proposerContactDetail.getPostalCode() : "";
        this.province = proposerContactDetail != null ? proposerContactDetail.getProvince() : "";
        this.town = proposerContactDetail != null ? proposerContactDetail.getTown() : "";
        this.emailAddress = proposerContactDetail != null ? proposerContactDetail.getEmailAddress() : "";
       this.contactPersonDetail = contactPersonDetail;
    }


    private List<ContactPersonDetailDto> transformContactPersonDetail(List<ProposerContactDetail.ContactPersonDetail> contactPersonDetail){
        return contactPersonDetail.parallelStream().map(new Function<ProposerContactDetail.ContactPersonDetail, ContactPersonDetailDto>() {
            @Override
            public ContactPersonDetailDto apply(ProposerContactDetail.ContactPersonDetail contactPersonDetail) {
                return new ContactPersonDetailDto(contactPersonDetail.getContactPersonName(),contactPersonDetail.getContactPersonEmail(),
                        contactPersonDetail.getMobileNumber(),contactPersonDetail.getWorkPhoneNumber());
            }
        }).collect(Collectors.toList());
    }
}
