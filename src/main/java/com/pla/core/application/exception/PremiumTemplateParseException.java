/*
 * Copyright (c) 3/31/15 9:29 AM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.application.exception;

/**
 * @author: Samir
 * @since 1.0 31/03/2015
 */
public class PremiumTemplateParseException extends RuntimeException {

    private PremiumTemplateParseException(String message) {
        super(message);
    }


    public static void raiseHeaderInvalidException() {
        throw new PremiumTemplateParseException("Header is not valid; headers should match with selected influencing factor");
    }

    public static void raiseNumberRowMismatchException(int expectedNoOfRow, int actualNoOfRow) {
        throw new PremiumTemplateParseException("No of row mismatching. Expected : " + expectedNoOfRow + " Actual : " + actualNoOfRow);
    }

    public static void raiseTemplateContainsEmptyRowInBetweenException() {
        throw new PremiumTemplateParseException("Not a valid premium template as it contains empty row");
    }

    public static void raiseNotValidTemplateException() {
        throw new PremiumTemplateParseException("Not a valid premium template");
    }
}
