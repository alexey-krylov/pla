package com.pla.grouphealth.claim.cashless.presentation.dto.claim;

import com.pla.grouphealth.sharedresource.model.vo.GHProposer;
import com.pla.grouphealth.sharedresource.model.vo.GHProposerContactDetail;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;

import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Author - Mohan Sharma Created on 2/4/2016.
 */
@Getter
@Setter
@NoArgsConstructor
public class GHProposerDto {

    private String proposerName;
    private String proposerCode;
    private GHProposerContactDetailDto contactDetail;

    public GHProposerDto updateWithDetails(GHProposer ghProposer) {
        this.proposerCode = ghProposer.getProposerCode();
        this.proposerName = ghProposer.getProposerName();
        this.contactDetail = constructContactDetails(ghProposer.getContactDetail());
        return this;
    }

    private GHProposerContactDetailDto constructContactDetails(GHProposerContactDetail contactDetail) {
        return new GHProposerContactDetailDto()
                .updateWithContacts(contactDetail);
    }
}
