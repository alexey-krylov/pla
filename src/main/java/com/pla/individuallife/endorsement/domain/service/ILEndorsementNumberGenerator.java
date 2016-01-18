package com.pla.individuallife.endorsement.domain.service;

import com.pla.sharedkernel.util.SequenceGenerator;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Raghu on 8/27/2015.
 */
@Component
public class ILEndorsementNumberGenerator {


    private SequenceGenerator sequenceGenerator;

    @Autowired
    public ILEndorsementNumberGenerator(SequenceGenerator sequenceGenerator) {
        this.sequenceGenerator = sequenceGenerator;
    }

    /**
     * @param clazz
     * @return
     */
    public String getEndorsementNumber(Class clazz, LocalDate now) {
        String ilEndorsementSequence = sequenceGenerator.getSequence(clazz);
        String currentDateInString = now.toString(DateTimeFormat.forPattern("dd/MM/yyyy"));
        String month = currentDateInString.substring(3, 5).trim();
        String year = currentDateInString.substring(8, 10).trim();
        //String endorsementNumber = 8 +"-"+2+"-"+ ilEndorsementSequence + month + year;
        String endorsementNumber = "8" + "-" + "2" + "-" + ilEndorsementSequence + "-" + month + year;
        return endorsementNumber;
    }
}
