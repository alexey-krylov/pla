package com.pla.individuallife.quotation.domain.model.proposal;

import com.pla.individuallife.proposal.domain.model.*;
import com.pla.sharedkernel.domain.model.Gender;
import com.pla.sharedkernel.domain.model.MaritalStatus;
import org.joda.time.DateTime;
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
    DateTime localDate = new DateTime("2015-05-26");
    EmploymentDetail employmentDetail;
    Address residentialAddress;

    @Test
    public void givenAProposalAssuredBuilder_whenAllTheSetUpIsCorrect_thenItShouldCreateProposalAssured(){

        proposedAssuredBuilder1=new ProposedAssuredBuilder()
                .withTitle("Dr.")
                .withFirstName("NthDimenzion")
                .withSurname("PvtLtd")
                .withEmailAddress("@XYZ.com")
                .withEmploymentDetail(new EmploymentDetailBuilder().createEmploymentDetail())
                .withGender(Gender.MALE).withDateOfBirth(localDate)
                .withMobileNumber("98765432")
                .withMaritalStatus(MaritalStatus.MARRIED)
                .withSpouseFirstName("NthDimenzion")
                .withSpouseLastName("PvtLtd")
                .withSpouseEmailAddress("abc@nthDimenzion")
                .withEmploymentDetail(new EmploymentDetailBuilder().createEmploymentDetail())
                .withResidentialAddress(new ResidentialAddress(new AddressBuilder().createAddress(), 897784243))
                .withIsProposer(true)
                .withNrc("123456/78/9");
        proposedAssured = proposedAssuredBuilder1.createProposedAssured();

        assertEquals(Gender.MALE, proposedAssured.getGender());
        assertThat("Dr.", is(proposedAssured.getTitle()));
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
