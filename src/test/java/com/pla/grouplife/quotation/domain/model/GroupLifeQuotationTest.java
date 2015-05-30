package com.pla.grouplife.quotation.domain.model;

import com.pla.core.domain.model.agent.AgentId;
import com.pla.grouplife.quotation.domain.event.ProposerAddedEvent;
import com.pla.grouplife.quotation.domain.event.QuotationClosedEvent;
import com.pla.grouplife.quotation.domain.exception.QuotationException;
import com.pla.grouplife.quotation.domain.model.*;
import com.pla.sharedkernel.identifier.QuotationId;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

/**
 * Created by Samir on 4/25/2015.
 */
public class GroupLifeQuotationTest {

    private QuotationId quotationId;

    private AgentId agentId;

    private GroupLifeQuotation groupLifeQuotation;

    @Before
    public void setUp() {
        quotationId = new QuotationId("11");
        agentId = new AgentId("121");
        ProposerBuilder proposerBuilder = Proposer.getProposerBuilder("NthDimenzion");
        Proposer proposer = proposerBuilder.build();
        groupLifeQuotation = GroupLifeQuotation.createWithAgentAndProposerDetail("5-1-100001-0415", "Admin", quotationId, agentId, proposer);
        assertNotNull(groupLifeQuotation);
    }

    @Test
    public void shouldCreateGLQuotationWithAgentDetail() {
        assertEquals("5-1-100001-0415", groupLifeQuotation.getQuotationNumber());
    }

    @Test(expected = QuotationException.class)
    public void itShouldThrowExceptionWhenAClosedQuotationGetsUpdated() {
        groupLifeQuotation.closeQuotation();
        groupLifeQuotation.updateWithAgent(new AgentId("22"));
    }

    @Test(expected = QuotationException.class)
    public void itShouldThrowExceptionWhenADeclinedQuotationGetsUpdated() {
        groupLifeQuotation.declineQuotation();
        groupLifeQuotation.updateWithAgent(new AgentId("22"));
    }

    @Test
    public void itShouldUpdateProposeDetailOfDraftedGLQuotation() {
        ProposerBuilder proposerBuilder = Proposer.getProposerBuilder("NthDimenzion", "Nth001").
                withContactDetail("5th Block", "Kormangla", "560076", "Karnataka", "Bangalore", "info@nthdimenzion.com")
                .withContactPersonDetail("Jones", "abc@gmail.com", "9916971270", "657576576");

        Proposer proposer = proposerBuilder.build();
        GroupLifeQuotation groupLifeQuotation = this.groupLifeQuotation.updateWithProposer(proposer);
        Proposer updatedProposerDetail = groupLifeQuotation.getProposer();

        ProposerContactDetail proposerContactDetail = proposerBuilder.getProposerContactDetail();
        ProposerContactDetail updatedProposerContactDetail = updatedProposerDetail.getContactDetail();
        ProposerContactDetail.ContactPersonDetail contactPersonDetail = proposerContactDetail.getContactPersonDetail();
        ProposerContactDetail.ContactPersonDetail updatedContactPersonDetail = updatedProposerContactDetail.getContactPersonDetail();

        assertEquals(proposerBuilder.getProposerName(), updatedProposerDetail.getProposerName());
        assertEquals(proposerBuilder.getProposerCode(), updatedProposerDetail.getProposerCode());
        assertEquals(proposerContactDetail.getAddressLine1(), updatedProposerContactDetail.getAddressLine1());
        assertEquals(proposerContactDetail.getAddressLine2(), updatedProposerContactDetail.getAddressLine2());
        assertEquals(proposerContactDetail.getEmailAddress(), updatedProposerContactDetail.getEmailAddress());
        assertEquals(proposerContactDetail.getPostalCode(), updatedProposerContactDetail.getPostalCode());
        assertEquals(proposerContactDetail.getProvince(), updatedProposerContactDetail.getProvince());
        assertEquals(proposerContactDetail.getTown(), updatedProposerContactDetail.getTown());
        assertEquals(contactPersonDetail.getContactPersonName(), updatedContactPersonDetail.getContactPersonName());
        assertEquals(contactPersonDetail.getContactPersonEmail(), updatedContactPersonDetail.getContactPersonEmail());
        assertEquals(contactPersonDetail.getMobileNumber(), updatedContactPersonDetail.getMobileNumber());
        assertEquals(contactPersonDetail.getWorkPhoneNumber(), updatedContactPersonDetail.getWorkPhoneNumber());

    }

    @Test
    public void givenADraftedGLQuotationItShouldUpdateWithAgentDetail() {
        AgentId newAgentId = new AgentId("10002");
        GroupLifeQuotation updatedGlQuotation = this.groupLifeQuotation.updateWithAgent(newAgentId);
        assertEquals(newAgentId, updatedGlQuotation.getAgentId());
    }


    @Test
    public void whenGLQuotationIsGeneratedItShouldRegisterProposerAddedEvent() {
        ProposerBuilder proposerBuilder = Proposer.getProposerBuilder("NthDimenzion", "Nth001").
                withContactDetail("5th Block", "Kormangla", "560076", "Karnataka", "Bangalore", "info@nthdimenzion.com")
                .withContactPersonDetail("Jones", "abc@gmail.com", "9916971270", "657576576");

        Proposer proposer = proposerBuilder.build();
        GroupLifeQuotation groupLifeQuotation = this.groupLifeQuotation.updateWithProposer(proposer);
        groupLifeQuotation.generateQuotation(LocalDate.now());
        ProposerContactDetail proposerContactDetail = proposerBuilder.getProposerContactDetail();
        ProposerAddedEvent proposerAddedEvent = new ProposerAddedEvent(proposerBuilder.getProposerName(), proposerBuilder.getProposerCode(),
                proposerContactDetail.getAddressLine1(), proposerContactDetail.getAddressLine2(),
                proposerContactDetail.getPostalCode(), proposerContactDetail.getProvince(), proposerContactDetail.getTown(), proposerContactDetail.getEmailAddress());
        ProposerAddedEvent registeredProposerEvent = (ProposerAddedEvent) groupLifeQuotation.getUncommittedEvents().peek().getPayload();
        assertThat(registeredProposerEvent, is(proposerAddedEvent));
    }


    @Test
    public void whenGlQuotationIsCloseItShouldRegisterQuotationClosedEvent() {
        this.groupLifeQuotation.closeQuotation();
        QuotationClosedEvent quotationClosedEvent = (QuotationClosedEvent) this.groupLifeQuotation.getUncommittedEvents().peek().getPayload();
        assertThat(new QuotationClosedEvent(this.groupLifeQuotation.getQuotationId()), is(quotationClosedEvent));
    }

    @Test
    public void isShouldReturnTrueWhenQuotationStatusIsGenerated() {
        this.groupLifeQuotation.generateQuotation(LocalDate.now());
        boolean requireVersioning = this.groupLifeQuotation.requireVersioning();
        assertTrue(requireVersioning);
    }

    @Test
    public void itShouldInactivateGLQuotation() {
        this.groupLifeQuotation.inactiveQuotation();
        assertEquals(QuotationStatus.INACTIVE, this.groupLifeQuotation.getQuotationStatus());
    }

    @Test
    public void itShouldDeclineQuotation() {
        this.groupLifeQuotation.declineQuotation();
        assertEquals(QuotationStatus.DECLINED, this.groupLifeQuotation.getQuotationStatus());
    }
}
