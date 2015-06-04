package com.pla.grouphealth.quotation.domain.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import static com.google.common.base.Preconditions.checkArgument;

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
}
