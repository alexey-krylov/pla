package com.pla.core.domain.model.generalinformation;

import com.google.common.collect.Sets;
import com.pla.sharedkernel.domain.model.ModalFactorItem;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by Admin on 4/15/2015.
 */
public class ModalFactorInformationUnitTest {

    Set<ModelFactorOrganizationInformation> modelFactorItems;

    @Before
    public void setUp(){
        modelFactorItems = Sets.newLinkedHashSet();
        ModelFactorOrganizationInformation modelFactorOrganizationInformation = new ModelFactorOrganizationInformation(ModalFactorItem.SEMI_ANNUAL,new BigDecimal(1000.99194));
        modelFactorItems.add(modelFactorOrganizationInformation);
        modelFactorOrganizationInformation = new ModelFactorOrganizationInformation(ModalFactorItem.QUARTERLY,new BigDecimal(1001.99994));
        modelFactorItems.add(modelFactorOrganizationInformation);
        modelFactorOrganizationInformation = new ModelFactorOrganizationInformation(ModalFactorItem.MONTHLY,new BigDecimal(1002.99899));
        modelFactorItems.add(modelFactorOrganizationInformation);

    }

    @Test
    public void givenModalFactorItem_whenModalFactorItemIsMonthly_thenItShouldReturnTheMonthlyModalFactor(){
        BigDecimal expectedMonthlyModalFactor = new BigDecimal(1002.99899).setScale(4,BigDecimal.ROUND_HALF_UP);
        BigDecimal monthlyModalFactor = ModelFactorOrganizationInformation.getMonthlyModalFactor(modelFactorItems);
        assertThat(expectedMonthlyModalFactor,is(monthlyModalFactor));
    }

    @Test
    public void givenModalFactorItem_whenModalFactorItemIsSemiAnnual_thenItShouldReturnTheSemiAnnualModalFactor(){
        BigDecimal expectedSemiAnnualModalFactor = new BigDecimal(1000.99194).setScale(4,BigDecimal.ROUND_HALF_UP);
        BigDecimal semiAnnualModalFactor = ModelFactorOrganizationInformation.getSemiAnnualModalFactor(modelFactorItems);
        assertThat(expectedSemiAnnualModalFactor,is(semiAnnualModalFactor));
    }

    @Test
    public void givenModalFactorItem_whenModalFactorItemIsQuarterly_thenItShouldReturnTheQuarterlyModalFactor(){
        BigDecimal expectedQuarterlyModalFactor = new BigDecimal(1001.99994).setScale(4,BigDecimal.ROUND_HALF_UP);
        BigDecimal quarterlyModalFactor = ModelFactorOrganizationInformation.getQuarterlyModalFactor(modelFactorItems);
        assertThat(expectedQuarterlyModalFactor,is(quarterlyModalFactor));
    }

    @Test
    public void given_when_then(){
        String nrc = "1111111111";
        String part1 = nrc.substring(0, 6);
        String part2 = nrc.substring(6, 8);
        String part3 = nrc.substring(8, 9);
        nrc = part1.concat("/").concat(part2).concat("/").concat(part3);
        System.out.print(nrc);
    }
}
