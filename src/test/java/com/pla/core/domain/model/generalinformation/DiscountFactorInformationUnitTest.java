package com.pla.core.domain.model.generalinformation;

import com.google.common.collect.Sets;
import com.pla.sharedkernel.domain.model.DiscountFactorItem;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by Admin on 4/15/2015.
 */
public class DiscountFactorInformationUnitTest {

    Set<DiscountFactorOrganizationInformation> discountFactorItems;

    @Before
    public void setUp(){
        discountFactorItems = Sets.newLinkedHashSet();
        DiscountFactorOrganizationInformation discountFactorOrganizationInformation  = new DiscountFactorOrganizationInformation(DiscountFactorItem.ANNUAL,new BigDecimal(1234.867744));
        discountFactorItems.add(discountFactorOrganizationInformation);
        discountFactorOrganizationInformation  = new DiscountFactorOrganizationInformation(DiscountFactorItem.SEMI_ANNUAL,new BigDecimal(55555.864989));
        discountFactorItems.add(discountFactorOrganizationInformation);
        discountFactorOrganizationInformation  = new DiscountFactorOrganizationInformation(DiscountFactorItem.QUARTERLY,new BigDecimal(44444.884648493));
        discountFactorItems.add(discountFactorOrganizationInformation);
    }

    @Test
    public void givenDiscountFactorItems_whenDiscountFactorItemIsAnnual_thenItShouldReturnTheAnnualDiscountFactor(){
        BigDecimal expectedAnnualDiscountFactor = new BigDecimal(1234.867744);
        BigDecimal annualDiscountFactor = DiscountFactorOrganizationInformation.getAnnualDiscountFactor(discountFactorItems);
        assertThat(expectedAnnualDiscountFactor,is(annualDiscountFactor));
    }

    @Test
    public void givenDiscountFactorItems_whenDiscountFactorItemIsSemiAnnual_thenItShouldReturnTheSemiAnnualDiscountFactor(){
        BigDecimal expectedSemiAnnualDiscountFactor = new BigDecimal(55555.864989);
        BigDecimal semiAnnualDiscountFactor = DiscountFactorOrganizationInformation.getSemiAnnualDiscountFactor(discountFactorItems);
        assertThat(expectedSemiAnnualDiscountFactor,is(semiAnnualDiscountFactor));
    }

    @Test
    public void givenDiscountFactorItems_whenDiscountFactorItemIsQuarterly_thenItShouldReturnTheQuarterlyDiscountFactor(){
        BigDecimal expectedQuarterlyDiscountFactor = new BigDecimal(44444.884648493);
        BigDecimal quarterlyDiscountFactor = DiscountFactorOrganizationInformation.getQuarterlyDiscountFactor(discountFactorItems);
        assertThat(expectedQuarterlyDiscountFactor,is(quarterlyDiscountFactor));
    }
}
