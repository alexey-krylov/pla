package com.pla.quotation.domain.model;

import org.joda.time.LocalDate;

/**
 * Created by Samir on 4/7/2015.
 */
public interface IQuotation {

    IQuotation closeQuotation();

    IQuotation inactiveQuotation();

    IQuotation declineQuotation();

    IQuotation generateQuotation(LocalDate generatedOn);

    boolean requireVersioning();

}
