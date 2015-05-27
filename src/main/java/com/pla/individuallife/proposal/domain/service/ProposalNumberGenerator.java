package com.pla.individuallife.proposal.domain.service;

import com.pla.individuallife.proposal.domain.model.ProposalAggregate;
import com.pla.sharedkernel.util.SequenceGenerator;
import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by pradyumna on 22-05-2015.
 */
@Component
public class ProposalNumberGenerator {

    private SequenceGenerator sequenceGenerator;

    @Autowired
    public ProposalNumberGenerator(SequenceGenerator sequenceGenerator) {
        this.sequenceGenerator = sequenceGenerator;
    }

    /**
     * @return transactionType-division-runningSequence-SystemMonthSystemYear
     */
    public String getProposalNumber() {
        String proposalNumber = sequenceGenerator.getSequence(ProposalAggregate.class);
        proposalNumber = StringUtils.leftPad(proposalNumber, 7, "0");
        String currentDateInString = LocalDate.now().toString(DateTimeFormat.forPattern("dd/MM/yyyy"));
        String month = currentDateInString.substring(3, 5).trim();
        String year = currentDateInString.substring(8, 10).trim();
        proposalNumber = new String("6-2-" + proposalNumber + "-" + month + year);
        return proposalNumber;
    }
}
