package com.pla.individuallife.quotation.domain.model;

import com.pla.core.domain.model.agent.AgentId;
import com.pla.individuallife.quotation.domain.exception.QuotationException;
import com.pla.sharedkernel.domain.model.Gender;
import com.pla.sharedkernel.identifier.PlanId;
import com.pla.sharedkernel.identifier.QuotationId;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Karunakar on 6/4/2015.
 */
public class IndividualLifeQuotationTest {

    private QuotationId quotationId;

    private AgentId agentId;

    private PlanId planId;

    private IndividualLifeQuotation individualLifeQuotation;

    ProposedAssured proposedAssured;

    @Before
    public void setUp() {
        quotationId = new QuotationId("11");
        agentId = new AgentId("121");
        planId = new PlanId(("1211"));
        ProposedAssuredBuilder proposedAssuredBuilder = ProposedAssured.proposedAssuredBuilder();
        proposedAssuredBuilder.withTitle("Mr").withFirstName("Jones").withSurname("Dean").withNrcNumber("123456");
        proposedAssured = proposedAssuredBuilder.build();
        individualLifeQuotation = IndividualLifeQuotation.createWithBasicDetail("5-2-300001-0415", "Admin", quotationId, agentId, proposedAssured, planId);
        assertNotNull(individualLifeQuotation);
    }

    @Test
    public void shouldCreateILQuotationWithAgentDetail() {
        assertEquals("5-2-300001-0415", individualLifeQuotation.getQuotationNumber());
    }

    @Test(expected = QuotationException.class)
    public void itShouldThrowExceptionWhenAClosedQuotationGetsUpdated() {
        individualLifeQuotation.closeQuotation();
        individualLifeQuotation.updateWithAssured(proposedAssured, false);
    }

    @Test(expected = QuotationException.class)
    public void itShouldThrowExceptionWhenADeclinedQuotationGetsUpdated() {
        individualLifeQuotation.declineQuotation();
        individualLifeQuotation.updateWithAssured(proposedAssured, false);
    }

    @Test
    public void itShouldUpdateProposeDetailOfDraftedILQuotation() {

        Proposer proposer = new Proposer("Mr" , "Jones", "Dean", "123456", new LocalDate("2000-05-05"), Gender.MALE, "78878888989", "dean.jones@gmail.com");
        IndividualLifeQuotation individualLifeQuotation = this.individualLifeQuotation.updateWithProposer(proposer, agentId);
        Proposer updatedProposerDetail = individualLifeQuotation.getProposer();

        assertEquals(proposer.getFirstName(), updatedProposerDetail.getFirstName());
        assertEquals(proposer.getTitle(), updatedProposerDetail.getTitle());
        assertEquals(proposer.getSurname(), updatedProposerDetail.getSurname());
        assertEquals(proposer.getNrcNumber(), updatedProposerDetail.getNrcNumber());
        assertEquals(proposer.getDateOfBirth(), updatedProposerDetail.getDateOfBirth());
        assertEquals(proposer.getGender(), updatedProposerDetail.getGender());
        assertEquals(proposer.getMobileNumber(), updatedProposerDetail.getMobileNumber());
        assertEquals(proposer.getEmailAddress(), updatedProposerDetail.getEmailAddress());

    }

    @Test
    public void itShouldUpdateProposedAssuredDetailOfDraftedILQuotation() {

        ProposedAssuredBuilder proposedAssuredBuilder = ProposedAssured.proposedAssuredBuilder();
        proposedAssuredBuilder.withTitle("Mr").withFirstName("Jones").withSurname("Dean").withNrcNumber("123456").withDateOfBirth(new LocalDate("2000-05-05")).withGender(Gender.MALE).withMobileNumber("78878888989").withEmailAddress("dean.jones@gmail.com").withOccupation("Accountant");
        ProposedAssured proposedAssured = proposedAssuredBuilder.build();

        IndividualLifeQuotation individualLifeQuotation = this.individualLifeQuotation.updateWithAssured(proposedAssured, false);
        ProposedAssured updatedProposedAssuredDetail = individualLifeQuotation.getProposedAssured();

        assertEquals(proposedAssuredBuilder.getFirstName(), updatedProposedAssuredDetail.getFirstName());
        assertEquals(proposedAssuredBuilder.getTitle(), updatedProposedAssuredDetail.getTitle());
        assertEquals(proposedAssuredBuilder.getSurname(), updatedProposedAssuredDetail.getSurname());
        assertEquals(proposedAssuredBuilder.getNrcNumber(), updatedProposedAssuredDetail.getNrcNumber());
        assertEquals(proposedAssuredBuilder.getDateOfBirth(), updatedProposedAssuredDetail.getDateOfBirth());
        assertEquals(proposedAssuredBuilder.getGender(), updatedProposedAssuredDetail.getGender());
        assertEquals(proposedAssuredBuilder.getMobileNumber(), updatedProposedAssuredDetail.getMobileNumber());
        assertEquals(proposedAssuredBuilder.getEmailAddress(), updatedProposedAssuredDetail.getEmailAddress());
        assertEquals(proposedAssuredBuilder.getOccupation(), updatedProposedAssuredDetail.getOccupation());

    }


    @Test
    public void isShouldReturnTrueWhenQuotationStatusIsGenerated() {
        this.individualLifeQuotation.generateQuotation(LocalDate.now());
        boolean requireVersioning = this.individualLifeQuotation.requireVersioning();
        assertTrue(requireVersioning);
    }

    @Test
    public void itShouldInactivateILQuotation() {
        this.individualLifeQuotation.purgeQuotation();
        assertEquals(ILQuotationStatus.PURGED, this.individualLifeQuotation.getIlQuotationStatus());
    }

    @Test
    public void itShouldDeclineQuotation() {
        this.individualLifeQuotation.declineQuotation();
        assertEquals(ILQuotationStatus.DECLINED, this.individualLifeQuotation.getIlQuotationStatus());
    }
}
