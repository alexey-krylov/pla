package com.pla.grouphealth.sharedresource.model.vo;

import com.pla.grouphealth.claim.cashless.presentation.dto.PreAuthorizationClaimantProposerDetail;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;
import org.nthdimenzion.utils.UtilValidator;

import static com.google.common.base.Preconditions.checkArgument;
import static org.nthdimenzion.utils.UtilValidator.*;

/**
 * Created by Samir on 4/7/2015.
 */
@ValueObject
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
@Setter(value = AccessLevel.PACKAGE)
public class GHProposer {

    private String proposerName;

    private String proposerCode;

    private GHProposerContactDetail contactDetail;

    GHProposer(GHProposerBuilder proposerBuilder) {
        checkArgument(proposerBuilder != null);
        this.proposerName = proposerBuilder.getProposerName();
        this.proposerCode = proposerBuilder.getProposerCode();
        this.contactDetail = proposerBuilder.getProposerContactDetail();
    }

    public static GHProposerBuilder getProposerBuilder(String proposerName) {
        return new GHProposerBuilder(proposerName);
    }

    public static GHProposerBuilder getProposerBuilder(String proposerName, String proposerCode) {
        return new GHProposerBuilder(proposerName, proposerCode);
    }

    public GHProposer updateWithProposerDetails(PreAuthorizationClaimantProposerDetail preAuthorizationClaimantProposerDetail) {
        if(isNotEmpty(preAuthorizationClaimantProposerDetail)) {
            this.proposerName = preAuthorizationClaimantProposerDetail.getProposerName();
            this.proposerCode = preAuthorizationClaimantProposerDetail.getProposerCode();
            this.contactDetail = updateWithContactDetail(preAuthorizationClaimantProposerDetail);
        }
        return this;
    }

    private GHProposerContactDetail updateWithContactDetail(PreAuthorizationClaimantProposerDetail preAuthorizationClaimantProposerDetail) {
        GHProposerContactDetail contactDetail = isNotEmpty(this.contactDetail) ? this.contactDetail : new GHProposerContactDetail();
        return contactDetail.updateWithContactDetails(preAuthorizationClaimantProposerDetail);
    }
}
