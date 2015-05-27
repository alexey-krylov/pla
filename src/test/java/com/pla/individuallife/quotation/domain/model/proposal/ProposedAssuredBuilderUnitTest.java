package com.pla.individuallife.quotation.domain.model.proposal;

import com.pla.individuallife.proposal.domain.model.Address;
import com.pla.individuallife.proposal.domain.model.EmploymentDetail;
import com.pla.individuallife.proposal.domain.model.ProposedAssured;
import com.pla.individuallife.proposal.domain.model.ProposedAssuredBuilder;
import com.pla.sharedkernel.domain.model.Gender;
import com.pla.sharedkernel.domain.model.MaritalStatus;
import com.pla.sharedkernel.domain.model.TitleEnum;
import org.joda.time.LocalDate;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

//import junit.framework.Assert;

/**
 * Created by Prasant on 22-May-15.
 */
public class ProposedAssuredBuilderUnitTest {
    ProposedAssuredBuilder proposedAssuredBuilder1;
    ProposedAssured proposedAssured;
    LocalDate localDate =new LocalDate("2015-05-26");
    EmploymentDetail employmentDetail;
    Address residentialAddress;

    @Test
    public void givenAProposalAssuredBuilder_whenAllTheSetUpIsCorrect_thenItShouldCreateProposalAssured(){

        proposedAssuredBuilder1=new ProposedAssuredBuilder()
                .withTitle(TitleEnum.DR)
                .withFirstName("NthDimenzion")
                .withSurname("PvtLtd")
                .withEmailAddress("@XYZ.com")
                .withEmploymentDetail(new EmploymentDetail())
                .withGender(Gender.MALE).withDateOfBirth(localDate)
                .withMobileNumber(98765432)
                .withMaritalStatus(MaritalStatus.MARRIED)
                .withSpouseFirstName("NthDimenzion")
                .withSpouseLastName("PvtLtd")
                .withSpouseEmailAddress("abc@nthDimenzion")
                .withEmploymentDetail(new EmploymentDetail())
                .withResidentialAddress(new Address())
                .withIsProposer(true)
                .withNrc("123456/78/9");
        proposedAssured = proposedAssuredBuilder1.createProposedAssured();

        assertEquals(Gender.MALE, proposedAssured.getGender());
        assertThat(TitleEnum.DR, is(proposedAssured.getTitle()));
        assertEquals("NthDimenzion", proposedAssured.getFirstName());
        assertEquals("PvtLtd",proposedAssured.getSurname());
        assertEquals("@XYZ.com",proposedAssured.getEmailAddress());
        assertEquals(localDate,proposedAssured.getDateOfBirth());
        assertEquals(98765432,proposedAssured.getMobileNumber());
        assertEquals(MaritalStatus.MARRIED,proposedAssured.getMaritalStatus());
        assertEquals("NthDimenzion",proposedAssured.getSpouseFirstName());
        assertEquals("PvtLtd",proposedAssured.getSpouseLastName());
        assertEquals("abc@nthDimenzion",proposedAssured.getSpouseEmailAddress());
        assertEquals("123456/78/9",proposedAssured.getNrc());
    }
}
