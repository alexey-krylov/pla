package com.pla.individuallife.proposal.domain.model;

import lombok.Getter;

/**
 * Created by pradyumna on 22-05-2015.
 */
@Getter
public class ProposalProcessor {

    private String userName;

    public ProposalProcessor(String userName) {
        this.userName = userName;
    }

}
