package com.pla.individuallife.proposal.domain.model;

/**
 * Created by pradyumna on 23-05-2015.
 */
public enum ProposalQuestion {

    HAVE_ANY_OTHER_POLICY_ISSUED_IN_LAST_12_MONTHS("Have any policies been issued on your life in the last 12 months by  any other insurer?"),
    ANY_OTHER_PROPOSAL_PENDING("Are any proposals for Life Assurance on your life pending with Professional Life Assurance Limited or any other insurer ?"),
    ANY_APPLICATION_DECLINED("Has an application for assurance on your life ever been declined, deferred, not proceeded with, loaded or accepted other than as submitted ?");

    String description;

    ProposalQuestion(String description) {
        this.description = description;
    }
}
