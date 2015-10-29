package com.pla.grouplife.endorsement.exception;

/**
 * Created by Hemant Neel on 29-Oct-15.
 */
public class GLEndorsementException extends RuntimeException {
    public GLEndorsementException(String message) {
        super(message);
    }

    public static void raiseMandatoryDocumentNotUploaded() {
        throw new GLEndorsementException("Mandatory Documents does not submitted by the processor");
    }
}
