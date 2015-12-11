package com.pla.grouplife.sharedresource.model.vo;

import com.google.common.collect.Lists;
import com.pla.grouplife.sharedresource.dto.ContactPersonDetailDto;
import lombok.Getter;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Samir on 4/8/2015.
 */
@Getter
public class ProposerBuilder {

    private String proposerName;

    private String proposerCode;

    private ProposerContactDetail proposerContactDetail;


    ProposerBuilder(String proposerName, String proposerCode) {
        checkArgument(isNotEmpty(proposerName));
        checkArgument(isNotEmpty(proposerCode));
        this.proposerName = proposerName;
        this.proposerCode = proposerCode;
    }

    ProposerBuilder(String proposerName) {
        checkArgument(isNotEmpty(proposerName));
        this.proposerName = proposerName;
    }


    public ProposerBuilder withContactDetail(String addressLine1, String addressLine2, String postalCode, String province, String town, String emailAddress) {
        this.proposerContactDetail = new ProposerContactDetail(addressLine1, addressLine2, postalCode, province, town, emailAddress);
        return this;
    }

    public ProposerBuilder withContactPersonDetail(List<ContactPersonDetailDto> contactPersonDetailDto) {
        contactPersonDetailDto = Lists.newArrayList();
        contactPersonDetailDto.add(new ContactPersonDetailDto("efwu","swr","4444444444","4444444444444"));
        checkArgument(proposerContactDetail != null);
        List<ProposerContactDetail.ContactPersonDetail> contactPersonDetail  = contactPersonDetailDto.parallelStream().map(new Function<ContactPersonDetailDto, ProposerContactDetail.ContactPersonDetail>() {
            @Override
            public ProposerContactDetail.ContactPersonDetail apply(ContactPersonDetailDto contactPersonDetailDto) {
                ProposerContactDetail proposerContactDetail1 = new ProposerContactDetail();
                return proposerContactDetail1.new ContactPersonDetail(contactPersonDetailDto.getContactPersonName(),contactPersonDetailDto.getContactPersonEmail(),
                        contactPersonDetailDto.getContactPersonMobileNumber(),contactPersonDetailDto.getContactPersonWorkPhoneNumber());
            }
        }).collect(Collectors.toList());
        this.proposerContactDetail = this.proposerContactDetail.addContactPersonDetail(contactPersonDetail);
        return this;
    }

    public Proposer build() {
        return new Proposer(this);
    }

}