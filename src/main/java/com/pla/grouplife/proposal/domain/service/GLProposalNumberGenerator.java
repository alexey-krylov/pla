package com.pla.grouplife.proposal.domain.service;

import com.pla.sharedkernel.util.SequenceGenerator;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Samir on 4/8/2015.
 */
@Component
public class GLProposalNumberGenerator {

    private SequenceGenerator sequenceGenerator;

    @Autowired
    public GLProposalNumberGenerator(SequenceGenerator sequenceGenerator) {
        this.sequenceGenerator = sequenceGenerator;
    }


    /**
     * @param transactionType
     * @param division
     * @param clazz
     * @return transactionType-division-runningSequence-SystemMonthSystemYear
     */
    public String getProposalNumber(String transactionType, String division, Class clazz,LocalDate now) {
        String proposalSequence = sequenceGenerator.getSequence(clazz);
        String currentDateInString = now.toString(DateTimeFormat.forPattern("dd/MM/yyyy"));
        String month = currentDateInString.substring(3, 5).trim();
        String year = currentDateInString.substring(8, 10).trim();
        String proposalNumber = transactionType + "-" + division + "-" + proposalSequence + "-" + month + year;
        return proposalNumber;
    }
}
