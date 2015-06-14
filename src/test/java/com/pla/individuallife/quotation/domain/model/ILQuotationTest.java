package com.pla.individuallife.quotation.domain.model;

import com.google.common.collect.Lists;
import com.pla.core.domain.model.agent.AgentId;
import com.pla.individuallife.quotation.domain.exception.QuotationException;
import com.pla.individuallife.quotation.domain.service.ILQuotationRoleAdapter;
import com.pla.sharedkernel.domain.model.Gender;
import com.pla.sharedkernel.identifier.PlanId;
import com.pla.sharedkernel.identifier.QuotationId;
import com.pla.sharedkernel.util.RolesUtil;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.nthdimenzion.security.service.UserLoginDetailDto;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Karunakar on 6/4/2015.
 */
public class ILQuotationTest {

    ProposedAssured proposedAssured;
    UserLoginDetailDto userLoginDetailDto;
    ILQuotationProcessor quotationProcessor;
    private QuotationId quotationId;
    private AgentId agentId;
    private PlanId planId;
    private ILQuotation quotation;

    @Before
    public void setUp() {
        quotationId = new QuotationId("11");
        agentId = new AgentId("121");
        planId = new PlanId(("1211"));
        userLoginDetailDto = UserLoginDetailDto.createUserLoginDetailDto("", "");
        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(RolesUtil.INDIVIDUAL_LIFE_QUOTATION_PROCESSOR_ROLE);
        List<SimpleGrantedAuthority> authorities = Lists.newArrayList();
        authorities.add(simpleGrantedAuthority);
        userLoginDetailDto.setAuthorities(authorities);
        quotationProcessor = ILQuotationRoleAdapter.userToQuotationProcessor(userLoginDetailDto);
        ProposedAssuredBuilder proposedAssuredBuilder = ProposedAssured.proposedAssuredBuilder();
        proposedAssuredBuilder.withTitle("Mr").withFirstName("Jones").withSurname("Dean").withNrcNumber("123456");
        proposedAssured = proposedAssuredBuilder.build();
        String quotationARId = "1000";
        quotation = quotation.createWithBasicDetail(quotationProcessor, quotationARId,
                "5-2-300001-0415", quotationId, agentId, proposedAssured, planId);
        assertNotNull(quotation);
    }

    @Test
    public void shouldCreateILQuotationWithAgentDetail() {
        assertEquals("5-2-300001-0415", quotation.getQuotationNumber());
    }

    @Test(expected = QuotationException.class)
    public void itShouldThrowExceptionWhenAClosedQuotationGetsUpdated() {
        quotation.closeQuotation();
        quotation.updateWithAssured(quotationProcessor, proposedAssured, false);
    }

    @Test(expected = QuotationException.class)
    public void itShouldThrowExceptionWhenADeclinedQuotationGetsUpdated() {
        quotation.declineQuotation();
        quotation.updateWithAssured(quotationProcessor, proposedAssured, false);
    }

    @Test
    public void itShouldUpdateProposeDetailOfDraftedILQuotation() {

        Proposer proposer = new Proposer("Mr", "Jones", "Dean", "123456", new DateTime("2000-05-05"), Gender.MALE, "78878888989", "dean.jones@gmail.com");
        this.quotation.updateWithProposer(quotationProcessor, proposer);
        Proposer updatedProposerDetail = quotation.getProposer();

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
        proposedAssuredBuilder.withTitle("Mr").withFirstName("Jones").withSurname("Dean").withNrcNumber("123456").withDateOfBirth(new DateTime("2000-05-05")).withGender(Gender.MALE).withMobileNumber("78878888989").withEmailAddress("dean.jones@gmail.com").withOccupation("Accountant");
        ProposedAssured proposedAssured = proposedAssuredBuilder.build();

        this.quotation.updateWithAssured(quotationProcessor, proposedAssured, false);
        ProposedAssured updatedProposedAssuredDetail = quotation.getProposedAssured();
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
        this.quotation.generateQuotation(LocalDate.now());
        boolean requireVersioning = this.quotation.requireVersioning();
        assertTrue(requireVersioning);
    }

    @Test
    public void itShouldInactivateILQuotation() {
        this.quotation.purgeQuotation();
        assertEquals(ILQuotationStatus.PURGED, this.quotation.getIlQuotationStatus());
    }

    @Test
    public void itShouldDeclineQuotation() {
        this.quotation.declineQuotation();
        assertEquals(ILQuotationStatus.DECLINED, this.quotation.getIlQuotationStatus());
    }
}
