package com.pla.grouphealth.quotation.application.service.exception;

/**
 * Created by Samir on 5/18/2015.
 */
public class GLInsuredTemplateExcelParseException extends RuntimeException {

    private GLInsuredTemplateExcelParseException(String message) {
        super(message);
    }

    public static void raiseNotValidHeaderException() {
        throw new  GLInsuredTemplateExcelParseException("Header is not valid.");
    }

    public static void raiseNotValidFirstHeaderException() {
        throw new  GLInsuredTemplateExcelParseException("First row should contain relationship as Self.");
    }

    public static void raiseFileIsBlank() {
        throw new GLInsuredTemplateExcelParseException("Please share either proposed assured details or number of proposed assured.");
    }

    public static void raiseNotSamePlanForAllCategoryException() {
        throw new  GLInsuredTemplateExcelParseException("For all similar relationship plan code should be same");
    }

    public static void raiseNotSamePlanForAllRelationshipException() {
        throw new  GLInsuredTemplateExcelParseException("For all similar category plan code should be same");
    }

    public static void raiseNotSamePlanForAllCategoryAndRelationshipException() {
        throw new  GLInsuredTemplateExcelParseException("All plan code should be same");
    }

    public static void raiseAssuredDataNotSharedException() {
        throw new  GLInsuredTemplateExcelParseException("Assured data not shared.");
    }

    public static void premiumLessThenMinimumConfiguredPremiumException() {
        throw new  GLInsuredTemplateExcelParseException("Total Premium is less than the specified Minimum for Plans.");
    }
    public static void noOfPersonsLessThenMinimumConfiguredPersonsException() {
        throw new  GLInsuredTemplateExcelParseException("Total Number of Members is less than the specified Minimum for Plans.");
    }

    public static void raiseSameOptionalCoverageException() {
        throw new  GLInsuredTemplateExcelParseException("Same optional cover added twice ...Please check");
    }

    public static void raiseNotValidValueException(String message) {
        throw new  GLInsuredTemplateExcelParseException(message);
    }

}
