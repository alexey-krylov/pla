package com.pla.grouphealth.domain.service;

import com.pla.sharedkernel.util.SequenceGenerator;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Karunakar on 4/30/2015.
 */
@Component
public class GHQuotationNumberGenerator {

    private SequenceGenerator sequenceGenerator;

    @Autowired
    public GHQuotationNumberGenerator(SequenceGenerator sequenceGenerator) {
        this.sequenceGenerator = sequenceGenerator;
    }


    /**
     * @param transactionType
     * @param division
     * @param clazz
     * @return transactionType-division-runningSequence-SystemMonthSystemYear
     */
    public String getQuotationNumber(String transactionType, String division, Class clazz) {
        String quotationSequence = sequenceGenerator.getSequence(clazz);
        String currentDateInString = LocalDate.now().toString(DateTimeFormat.forPattern("dd/MM/yyyy"));
        String month = currentDateInString.substring(3, 5).trim();
        String year = currentDateInString.substring(8, 10).trim();
        String quotationNumber = transactionType + "-" + division + "-" + quotationSequence + "-" + month + year;
        return quotationNumber;
    }
}
