package com.pla.individuallife.endorsement.domain.service;

import com.pla.grouphealth.sharedresource.query.GHFinder;
import com.pla.sharedkernel.util.SequenceGenerator;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Raghu Bandi on 27/10/2015.
 */
@Component
public class ILEndorsementRequestNumberGenerator {

    private SequenceGenerator sequenceGenerator;

    @Autowired
    public ILEndorsementRequestNumberGenerator(SequenceGenerator sequenceGenerator) {
        this.sequenceGenerator = sequenceGenerator;
    }

    public String getEndorsementRequestNumber(Class clazz) {
        String sequence = sequenceGenerator.getSequence(clazz);
        String currentDateInString = LocalDate.now().toString(DateTimeFormat.forPattern("dd/MM/yyyy"));
        String month = currentDateInString.substring(3, 5).trim();
        String year = currentDateInString.substring(8, 10).trim();
        String endorsementNumber = "3" + "-" + "2" + "-" + sequence + "-" + month + year;
        return endorsementNumber;
    }
}
