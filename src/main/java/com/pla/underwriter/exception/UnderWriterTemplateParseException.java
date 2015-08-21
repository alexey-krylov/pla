package com.pla.underwriter.exception;

/**
 * Created by Admin on 5/12/2015.
 */

public class UnderWriterTemplateParseException extends RuntimeException {

    private UnderWriterTemplateParseException(String message) {
        super(message);
    }

    public static void raiseHeaderInvalidException() {
        throw new UnderWriterTemplateParseException("Header is not valid; headers should match with selected influencing factor");
    }

    public static void raiseNumberCellMismatchException(int expectedNoOfRow, int actualNoOfRow) {
        throw new UnderWriterTemplateParseException("No of Cell mismatching. Expected : " + expectedNoOfRow + " Actual : " + actualNoOfRow);
    }

    public static void raiseTemplateContainsEmptyRowInBetweenException() {
        throw new UnderWriterTemplateParseException("Not a valid Under Writing Routing Level template as it contains empty row");
    }

    public static void raiseNotValidTemplateException() {
        throw new UnderWriterTemplateParseException("Not a valid Under Writing Routing Level template");
    }

    public static void raiseNotValidPlanCodeException() {
        throw new UnderWriterTemplateParseException("Not a valid plan");
    }

    public static void raiseNotValidPlanCoverageException() {
        throw new UnderWriterTemplateParseException("Not a valid plan coverage");
    }

    public static void raiseInfluencingFactorAreOverLapping(String influencingFactor){
        throw new UnderWriterTemplateParseException(influencingFactor+ " is overLapping");
    }
}
