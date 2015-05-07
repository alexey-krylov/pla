package com.pla.grouphealth.domain.model.quotation;

import com.pla.core.domain.model.agent.AgentId;
import com.pla.grouphealth.domain.event.quotation.ProposerAddedEvent;
import com.pla.grouphealth.domain.event.quotation.QuotationClosedEvent;
import com.pla.grouphealth.domain.exception.QuotationException;
import com.pla.grouphealth.domain.model.GHQuotationStatus;
import com.pla.sharedkernel.identifier.QuotationId;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

/**
 * Created by Karunakar on 05/07/2015.
 */
public class GroupHealthQuotationTest {

    private QuotationId quotationId;

    private AgentId agentId;

    private GroupHealthQuotation groupHealthQuotation;

    @Before
    public void setUp() {
        quotationId = new QuotationId("11");
        agentId = new AgentId("121");
        ProposerBuilder proposerBuilder = Proposer.getProposerBuilder("NthDimenzion");
        Proposer proposer = proposerBuilder.build();
        groupHealthQuotation = GroupHealthQuotation.createWithAgentAndProposerDetail("5-4-100001-0415", "Admin", quotationId, agentId, proposer);
        assertNotNull(groupHealthQuotation);
    }

    @Test
    public void shouldCreateGLQuotationWithAgentDetail() {
        assertEquals("5-4-100001-0415", groupHealthQuotation.getQuotationNumber());
    }

    @Test(expected = QuotationException.class)
    public void itShouldThrowExceptionWhenAClosedQuotationGetsUpdated() {
        groupHealthQuotation.closeQuotation();
        groupHealthQuotation.updateWithAgent(new AgentId("22"));
    }

    @Test(expected = QuotationException.class)
    public void itShouldThrowExceptionWhenADeclinedQuotationGetsUpdated() {
        groupHealthQuotation.declineQuotation();
        groupHealthQuotation.updateWithAgent(new AgentId("22"));
    }

    @Test
    public void itShouldUpdateProposeDetailOfDraftedGLQuotation() {
        ProposerBuilder proposerBuilder = Proposer.getProposerBuilder("NthDimenzion", "Nth001").
                withContactDetail("5th Block", "Kormangla", "560076", "Karnataka", "Bangalore", "info@nthdimenzion.com")
                .withContactPersonDetail("Jones", "abc@gmail.com", "9916971270", "657576576");

        Proposer proposer = proposerBuilder.build();
        GroupHealthQuotation groupHealthQuotation = this.groupHealthQuotation.updateWithProposer(proposer);
        Proposer updatedProposerDetail = groupHealthQuotation.getProposer();

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
        GroupHealthQuotation updatedGHQuotation = this.groupHealthQuotation.updateWithAgent(newAgentId);
        assertEquals(newAgentId, updatedGHQuotation.getAgentId());
    }


    @Test
    public void whenGLQuotationIsGeneratedItShouldRegisterProposerAddedEvent() {
        ProposerBuilder proposerBuilder = Proposer.getProposerBuilder("NthDimenzion", "Nth001").
                withContactDetail("5th Block", "Kormangla", "560076", "Karnataka", "Bangalore", "info@nthdimenzion.com")
                .withContactPersonDetail("Jones", "abc@gmail.com", "9916971270", "657576576");

        Proposer proposer = proposerBuilder.build();
        GroupHealthQuotation groupHealthQuotation = this.groupHealthQuotation.updateWithProposer(proposer);
        groupHealthQuotation.generateQuotation(LocalDate.now());
        ProposerContactDetail proposerContactDetail = proposerBuilder.getProposerContactDetail();
        ProposerAddedEvent proposerAddedEvent = new ProposerAddedEvent(proposerBuilder.getProposerName(), proposerBuilder.getProposerCode(),
                proposerContactDetail.getAddressLine1(), proposerContactDetail.getAddressLine2(),
                proposerContactDetail.getPostalCode(), proposerContactDetail.getProvince(), proposerContactDetail.getTown(), proposerContactDetail.getEmailAddress());
        ProposerAddedEvent registeredProposerEvent = (ProposerAddedEvent) groupHealthQuotation.getUncommittedEvents().peek().getPayload();
        assertThat(registeredProposerEvent, is(proposerAddedEvent));
    }


    @Test
    public void whenGlQuotationIsCloseItShouldRegisterQuotationClosedEvent() {
        this.groupHealthQuotation.closeQuotation();
        QuotationClosedEvent quotationClosedEvent = (QuotationClosedEvent) this.groupHealthQuotation.getUncommittedEvents().peek().getPayload();
        assertThat(new QuotationClosedEvent(this.groupHealthQuotation.getQuotationId()), is(quotationClosedEvent));
    }

    @Test
    public void isShouldReturnTrueWhenQuotationStatusIsGenerated() {
        this.groupHealthQuotation.generateQuotation(LocalDate.now());
        boolean requireVersioning = this.groupHealthQuotation.requireVersioning();
        assertTrue(requireVersioning);
    }

    @Test
    public void itShouldInactivateGLQuotation() {
        this.groupHealthQuotation.inactiveQuotation();
        assertEquals(GHQuotationStatus.INACTIVE, this.groupHealthQuotation.getGhQuotationStatus());
    }

    @Test
    public void itShouldDeclineQuotation() {
        this.groupHealthQuotation.declineQuotation();
        assertEquals(GHQuotationStatus.DECLINED, this.groupHealthQuotation.getGhQuotationStatus());
    }
}
