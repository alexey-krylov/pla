package com.pla.grouplife.endorsement.domain.service;

import com.pla.sharedkernel.util.SequenceGenerator;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Samir on 8/27/2015.
 */
@Component
public class GLEndorsementNumberGenerator {


    private SequenceGenerator sequenceGenerator;

    @Autowired
    public GLEndorsementNumberGenerator(SequenceGenerator sequenceGenerator) {
        this.sequenceGenerator = sequenceGenerator;
    }

    /**
     * @param clazz
     * @return
     */
    public String getEndorsementNumber(Class clazz, LocalDate now) {
        String glEndorsementSequence = sequenceGenerator.getSequence(clazz);
        String currentDateInString = now.toString(DateTimeFormat.forPattern("dd/MM/yyyy"));
        String month = currentDateInString.substring(3, 5).trim();
        String year = currentDateInString.substring(8, 10).trim();
        String endorsementNumber = 31 + glEndorsementSequence + month + year;
        return endorsementNumber;
    }
}
