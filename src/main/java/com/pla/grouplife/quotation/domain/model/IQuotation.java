package com.pla.grouplife.quotation.domain.model;

import org.joda.time.LocalDate;

/**
 * Created by Samir on 4/7/2015.
 */
public interface IQuotation {

    void closeQuotation();

    void purgeQuotation();

    void declineQuotation();

    void generateQuotation(LocalDate generatedOn);

    boolean requireVersioning();

}
