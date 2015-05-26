package com.pla.individuallife.domain.model.proposal;

import com.pla.sharedkernel.domain.model.Gender;
import com.pla.sharedkernel.domain.model.MaritalStatus;
import com.pla.sharedkernel.domain.model.TitleEnum;
import junit.framework.Assert;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Created by Prasant on 22-May-15.
 */
public class ProposedAssuredBuilderUnitTest {
    ProposedAssuredBuilder proposedAssuredBuilder1;
    ProposedAssured proposedAssured;
    LocalDate localDate =new LocalDate("2015-05-26");
    EmploymentDetail employmentDetail;
    Address residentialAddress;

    @Before
    public void setUp()
    {
        proposedAssuredBuilder1=new ProposedAssuredBuilder().withTitle(TitleEnum.DR).withFirstName("NthDimenzion")
        .withSurname("PvtLtd") .withEmailAddress("@XYZ.com").withEmploymentDetail(new EmploymentDetail()).withGender(Gender.MALE).withDateOfBirth(localDate)
                .withMobileNumber(98765432).withMaritalStatus(MaritalStatus.MARRIED).withSpouseFirstName("NthDimenzion").withSpouseLastName("PvtLtd").withSpouseEmailAddress("abc@nthDimenzion")
        .withEmploymentDetail(new EmploymentDetail()).withResidentialAddress(new Address()).withIsProposer(true).withNrc("123456/78/9");

        proposedAssured = proposedAssuredBuilder1.createProposedAssured();

    }
    @Test
    public void givenMale_thenItShouldReturnTheproposedObjectForGivenGender()
    {
        Assert.assertEquals(Gender.MALE,proposedAssured.getGender());
    }

    @Test
    public void givenTitle_thenItShouldReturTheproposedObjectForGivenTitle()
    {
        assertThat(TitleEnum.DR, is(proposedAssured.getTitle()));
    }

    @Test
    public void givenFirstName_thenItShouldReturTheproposedObjectForGivenFirstName()
    {
        Assert.assertEquals("NthDimenzion", proposedAssured.getFirstName());
    }

    @Test
    public void givenSurName_thenItShouldReturTheproposedObjectForGivenSurName()
    {
        Assert.assertEquals("PvtLtd",proposedAssured.getSurname());
    }

    @Test
    public void givenEmail_thenItShouldReturTheproposedObjectForGivenEmailAddress()
    {
        Assert.assertEquals("@XYZ.com",proposedAssured.getEmailAddress());
    }

    @Test
    public void givenDateOfBirth_thenItShouldReturTheproposedObjectForGivenDateOfBirth()
    {
        Assert.assertEquals(localDate,proposedAssured.getDateOfBirth());
    }

    @Test
    public void givenMobileNumber_thenItShouldReturTheproposedObjectForGivenMobileNumber()
    {
        Assert.assertEquals(98765432,proposedAssured.getMobileNumber());
    }

    @Test
    public void givenMartialStatus_thenItShouldReturTheproposedObjectForGivenMaritialStatus()
    {
        Assert.assertEquals(MaritalStatus.MARRIED,proposedAssured.getMaritalStatus());
    }

    @Test
    public void givenSpouseFirstName_thenItShouldReturTheproposedObjectForGivenSpouseFirstName()
    {
        Assert.assertEquals("NthDimenzion",proposedAssured.getSpouseFirstName());
    }

    @Test
    public void givenSpouseLastName_thenItShouldReturTheproposedObjectForGivenSpouseLastName()
    {
        Assert.assertEquals("PvtLtd",proposedAssured.getSpouseLastName());
    }

    @Test
    public void givenSpouseEmailAddress_thenItShouldReturTheproposedObjectForGivenSpouseEmailAddress()
    {
        Assert.assertEquals("abc@nthDimenzion",proposedAssured.getSpouseEmailAddress());
    }

    @Test
    public void givenwithEmploymentDetail_thenItShouldReturTheproposedObjectForGivenwithEmploymentDetail()
    {
        Assert.assertEquals("abc@nthDimenzion",proposedAssured.getSpouseEmailAddress());
    }

    @Test
    public void givenNrc_thenItShouldReturTheproposedObjectForGivenNrc()
    {
        Assert.assertEquals("123456/78/9",proposedAssured.getNrc());
    }

}
