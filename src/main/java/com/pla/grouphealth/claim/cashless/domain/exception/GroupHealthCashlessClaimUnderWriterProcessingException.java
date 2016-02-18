package com.pla.grouphealth.claim.cashless.domain.exception;

/**
 * Created by Mohan Sharma on 18-05-2015.
 */
public class GroupHealthCashlessClaimUnderWriterProcessingException extends Exception {

    private String underwriterLevel;

    public GroupHealthCashlessClaimUnderWriterProcessingException(String underwriterLevel, String message) {
        super(message);
        this.underwriterLevel = underwriterLevel;
    }

    public String getUnderwriterLevel() {
        return underwriterLevel;
    }

    public void setUnderwriterLevel(String underwriterLevel) {
        this.underwriterLevel = underwriterLevel;
    }
}
