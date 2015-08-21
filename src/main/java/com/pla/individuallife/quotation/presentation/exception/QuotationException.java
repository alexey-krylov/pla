package com.pla.individuallife.quotation.presentation.exception;

import java.math.BigDecimal;

/**
 * Created by Admin on 8/7/2015.
 */
public class QuotationException extends RuntimeException{

    public QuotationException(String message) {
        super(message);
    }


    public static void raiseInvalidSumAssuredException(BigDecimal minimumSumAssured, BigDecimal maximumSumAssured, Integer multiplesOf){
        throw  new QuotationException("Please enter multiples of "+multiplesOf);
    }

}
