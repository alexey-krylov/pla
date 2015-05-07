package com.pla.grouphealth.domain.model.quotation;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Created by Karunakar on 4/30/2015.
 */
@ValueObject
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
@Setter(value = AccessLevel.PACKAGE)
public class Proposer {

    private String proposerName;

    private String proposerCode;

    private ProposerContactDetail contactDetail;

    Proposer(ProposerBuilder proposerBuilder) {
        checkArgument(proposerBuilder != null);
        this.proposerName = proposerBuilder.getProposerName();
        this.proposerCode = proposerBuilder.getProposerCode();
        this.contactDetail = proposerBuilder.getProposerContactDetail();
    }

    public static ProposerBuilder getProposerBuilder(String proposerName) {
        return new ProposerBuilder(proposerName);
    }

    public static ProposerBuilder getProposerBuilder(String proposerName, String proposerCode) {
        return new ProposerBuilder(proposerName, proposerCode);
    }
}
