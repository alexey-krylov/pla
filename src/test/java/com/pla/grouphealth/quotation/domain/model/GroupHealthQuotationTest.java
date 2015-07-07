package com.pla.grouphealth.quotation.domain.model;

import com.pla.core.domain.model.agent.AgentId;
import com.pla.grouphealth.quotation.domain.event.GHQuotationConvertedEvent;
import com.pla.grouphealth.quotation.domain.exception.GHQuotationException;
import com.pla.grouphealth.sharedresource.model.vo.GHProposer;
import com.pla.grouphealth.sharedresource.model.vo.GHProposerBuilder;
import com.pla.grouphealth.sharedresource.model.vo.GHProposerContactDetail;
import com.pla.sharedkernel.event.GHProposerAddedEvent;
import com.pla.sharedkernel.identifier.QuotationId;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

/**
 * Created by Samir on 4/25/2015.
 */
public class GroupHealthQuotationTest {

    private QuotationId quotationId;

    private AgentId agentId;

    private GroupHealthQuotation groupHealthQuotation;

    @Before
    public void setUp() {
        quotationId = new QuotationId("11");
        agentId = new AgentId("121");
        GHProposerBuilder proposerBuilder = GHProposer.getProposerBuilder("NthDimenzion");
        GHProposer proposer = proposerBuilder.build();
        groupHealthQuotation = GroupHealthQuotation.createWithAgentAndProposerDetail("5-1-100001-0415", "Admin", quotationId, agentId, proposer);
        assertNotNull(groupHealthQuotation);
    }

    @Test
    public void shouldCreateGLQuotationWithAgentDetail() {
        assertEquals("5-1-100001-0415", groupHealthQuotation.getQuotationNumber());
    }

    @Test(expected = GHQuotationException.class)
    public void itShouldThrowExceptionWhenAClosedQuotationGetsUpdated() {
        groupHealthQuotation.closeQuotation();
        groupHealthQuotation.updateWithAgent(new AgentId("22"));
    }

    @Test(expected = GHQuotationException.class)
    public void itShouldThrowExceptionWhenADeclinedQuotationGetsUpdated() {
        groupHealthQuotation.declineQuotation();
        groupHealthQuotation.updateWithAgent(new AgentId("22"));
    }

    @Test
    public void itShouldUpdateProposeDetailOfDraftedGLQuotation() {
        GHProposerBuilder proposerBuilder = GHProposer.getProposerBuilder("NthDimenzion", "Nth001").
                withContactDetail("5th Block", "Kormangla", "560076", "Karnataka", "Bangalore", "info@nthdimenzion.com")
                .withContactPersonDetail("Jones", "abc@gmail.com", "9916971270", "657576576");

        GHProposer proposer = proposerBuilder.build();
        GroupHealthQuotation groupHealthQuotation = this.groupHealthQuotation.updateWithProposer(proposer);
        GHProposer updatedProposerDetail = groupHealthQuotation.getProposer();

        GHProposerContactDetail proposerContactDetail = proposerBuilder.getProposerContactDetail();
        GHProposerContactDetail updatedProposerContactDetail = updatedProposerDetail.getContactDetail();
        GHProposerContactDetail.ContactPersonDetail contactPersonDetail = proposerContactDetail.getContactPersonDetail();
        GHProposerContactDetail.ContactPersonDetail updatedContactPersonDetail = updatedProposerContactDetail.getContactPersonDetail();

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
        GroupHealthQuotation updatedGlQuotation = this.groupHealthQuotation.updateWithAgent(newAgentId);
        assertEquals(newAgentId, updatedGlQuotation.getAgentId());
    }


    @Test
    public void whenGLQuotationIsGeneratedItShouldRegisterProposerAddedEvent() {
        GHProposerBuilder proposerBuilder = GHProposer.getProposerBuilder("NthDimenzion", "Nth001").
                withContactDetail("5th Block", "Kormangla", "560076", "Karnataka", "Bangalore", "info@nthdimenzion.com")
                .withContactPersonDetail("Jones", "abc@gmail.com", "9916971270", "657576576");

        GHProposer proposer = proposerBuilder.build();
        GroupHealthQuotation groupHealthQuotation = this.groupHealthQuotation.updateWithProposer(proposer);
        groupHealthQuotation.generateQuotation(LocalDate.now());
        GHProposerContactDetail proposerContactDetail = proposerBuilder.getProposerContactDetail();
        GHProposerAddedEvent GHProposerAddedEvent = new GHProposerAddedEvent(proposerBuilder.getProposerName(), proposerBuilder.getProposerCode(),
                proposerContactDetail.getAddressLine1(), proposerContactDetail.getAddressLine2(),
                proposerContactDetail.getPostalCode(), proposerContactDetail.getProvince(), proposerContactDetail.getTown(), proposerContactDetail.getEmailAddress());
        GHProposerAddedEvent registeredProposerEvent = (GHProposerAddedEvent) groupHealthQuotation.getUncommittedEvents().peek().getPayload();
        assertThat(registeredProposerEvent, is(GHProposerAddedEvent));
    }


    @Test
    public void whenGlQuotationIsCloseItShouldRegisterQuotationClosedEvent() {
        this.groupHealthQuotation.closeQuotation();
        GHQuotationConvertedEvent ghQuotationConvertedEvent = (GHQuotationConvertedEvent) this.groupHealthQuotation.getUncommittedEvents().peek().getPayload();
        assertThat(new GHQuotationConvertedEvent(this.groupHealthQuotation.getQuotationId()), is(ghQuotationConvertedEvent));
    }

    @Test
    public void isShouldReturnTrueWhenQuotationStatusIsGenerated() {
        this.groupHealthQuotation.generateQuotation(LocalDate.now());
        boolean requireVersioning = this.groupHealthQuotation.requireVersioning();
        assertTrue(requireVersioning);
    }

    @Test
    public void itShouldInactivateGLQuotation() {
        this.groupHealthQuotation.purgeQuotation();
        assertEquals(GHQuotationStatus.PURGED, this.groupHealthQuotation.getQuotationStatus());
    }

    @Test
    public void itShouldDeclineQuotation() {
        this.groupHealthQuotation.declineQuotation();
        assertEquals(GHQuotationStatus.DECLINED, this.groupHealthQuotation.getQuotationStatus());
    }
}
