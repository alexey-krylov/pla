package com.pla.grouphealth.policy.domain.service;

import com.pla.core.domain.model.agent.AgentId;
import com.pla.grouphealth.sharedresource.query.GHFinder;
import com.pla.sharedkernel.util.SequenceGenerator;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by Hemant Neel on 27/10/2015.
 */
@Component
public class GHPolicyEndorsementNumberGenerator {

    private SequenceGenerator sequenceGenerator;

    private GHFinder ghFinder;

    @Autowired
    public GHPolicyEndorsementNumberGenerator(SequenceGenerator sequenceGenerator, GHFinder ghFinder) {
        this.sequenceGenerator = sequenceGenerator;
        this.ghFinder = ghFinder;
    }

    public String getPolicyNumber(Class clazz) {
        String sequence = sequenceGenerator.getSequence(clazz);
        String currentDateInString = LocalDate.now().toString(DateTimeFormat.forPattern("dd/MM/yyyy"));
        String month = currentDateInString.substring(3, 5).trim();
        String year = currentDateInString.substring(8, 10).trim();
        String policyNumber = "8" + "-" + "1" + "-" + sequence + "-" + month + year;
        return policyNumber;
    }
}
