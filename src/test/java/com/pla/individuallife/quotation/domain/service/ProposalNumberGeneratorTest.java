package com.pla.individuallife.quotation.domain.service;

import com.pla.individuallife.proposal.domain.service.ProposalNumberGenerator;
import com.pla.sharedkernel.util.SequenceGenerator;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.when;

/**
 * Created by pradyumna on 22-05-2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class ProposalNumberGeneratorTest {

    @Mock
    private SequenceGenerator sequenceGenerator;

    @Before
    public void setup() {
        when(sequenceGenerator.getSequence(anyObject())).thenReturn("1");
    }

    @Test
    public void testGeneration() {
        ProposalNumberGenerator proposalNumberGenerator = new ProposalNumberGenerator(sequenceGenerator);
        String proposalNumber = proposalNumberGenerator.getProposalNumber();
        String currentDateInString = LocalDate.now().toString(DateTimeFormat.forPattern("dd/MM/yyyy"));
        String month = currentDateInString.substring(3, 5).trim();
        String year = currentDateInString.substring(8, 10).trim();
        Assert.assertEquals("6-2-0000001-"+month+year, proposalNumber);
    }
}
