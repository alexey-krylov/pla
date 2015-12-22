package com.pla.grouplife.claim.domain.service;
import com.pla.sharedkernel.util.SequenceGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
/**
 * Created by ak
 */
@Component
public class ClaimNumberGenerator {

    private SequenceGenerator sequenceGenerator;

    @Autowired
    public ClaimNumberGenerator(SequenceGenerator sequenceGenerator){
        this.sequenceGenerator=sequenceGenerator;
    }

    public String getClaimNumber(Class clazz, LocalDate now) {
        String claimSequence = sequenceGenerator.getSequence(clazz);
        String currentDateInString = now.toString(DateTimeFormat.forPattern("dd/MM/yyyy"));
        String month = currentDateInString.substring(3, 5).trim();
        String year = currentDateInString.substring(8, 10).trim();
        String claimNumber = "71"   + claimSequence + month + year;
        return claimNumber;
    }
}


