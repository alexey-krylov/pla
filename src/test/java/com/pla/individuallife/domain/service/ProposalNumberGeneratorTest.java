package com.pla.individuallife.domain.service;

import com.pla.sharedkernel.util.SequenceGenerator;
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
        Assert.assertEquals("6-2-0000001-0515", proposalNumber);
    }
}
