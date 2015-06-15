package com.pla.individuallife.quotation.domain.service;

import com.pla.sharedkernel.util.SequenceGenerator;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Karunakar on 4/30/2015.
 */
@Component
public class ILQuotationNumberGenerator {

    private SequenceGenerator sequenceGenerator;

    @Autowired
    public ILQuotationNumberGenerator(SequenceGenerator sequenceGenerator) {
        this.sequenceGenerator = sequenceGenerator;
    }


    /**
     * @param clazz
     * @return transactionType-division-runningSequence-SystemMonthSystemYear
     */
    public String getQuotationNumber(Class clazz) {
        String quotationSequence = sequenceGenerator.getSequence(clazz);
        String currentDateInString = LocalDate.now().toString(DateTimeFormat.forPattern("dd/MM/yyyy"));
        String month = currentDateInString.substring(3, 5).trim();
        String year = currentDateInString.substring(8, 10).trim();
        String quotationNumber = "5-2-" + quotationSequence + "-" + month + year;
        return quotationNumber;
    }
}
