package com.pla.core.domain.model.generalinformation;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pla.sharedkernel.domain.model.DiscountFactorItem;
import com.pla.sharedkernel.domain.model.ModalFactorItem;
import com.pla.sharedkernel.domain.model.Tax;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * Created by Admin on 4/6/2015.
 */
public class OrganizationGeneralInformationUnitTest {

    @Test
    public void givenServiceTaxOrganizationInformation_whenTheFactorsValuesAreInMoreThan3DecimalPlaces_thenItShouldLimitToThreeDecimalPlacesAndAddTheEntryToTheOrganizationGeneralInformation(){
        Map<Tax,BigDecimal> serviceTaxMap = Maps.newLinkedHashMap();
        serviceTaxMap.put(Tax.SERVICE_TAX, new BigDecimal(123.9567));

        OrganizationGeneralInformation organizationGeneralInformation = OrganizationGeneralInformation.createOrganizationGeneralInformation("OI001");
        organizationGeneralInformation.withServiceTaxOrganizationInformation(serviceTaxMap);

        BigDecimal expectedServiceTaxValue = new BigDecimal(123.96).setScale(2, BigDecimal.ROUND_HALF_UP);

        assertNotNull(organizationGeneralInformation.getServiceTax());
        assertThat(Tax.SERVICE_TAX,is(organizationGeneralInformation.getServiceTax().getTax()));
        assertThat(expectedServiceTaxValue,is(organizationGeneralInformation.getServiceTax().getValue()));
    }

    @Test
    public void givenDiscountFactorOrganizationInformation_thenItShouldAddTheEntryToTheOrganizationInformation(){
        List<Map<DiscountFactorItem,BigDecimal>> listOfDiscountFactorItem  = Lists.newArrayList();
        Map<DiscountFactorItem,BigDecimal> discountFactorMap = Maps.newLinkedHashMap();
        discountFactorMap.put(DiscountFactorItem.QUARTERLY, new BigDecimal(114.123449));
        listOfDiscountFactorItem.add(discountFactorMap);

        discountFactorMap = Maps.newLinkedHashMap();
        discountFactorMap.put(DiscountFactorItem.ANNUAL, new BigDecimal(111.123441));
        listOfDiscountFactorItem.add(discountFactorMap);

        discountFactorMap = Maps.newLinkedHashMap();
        discountFactorMap.put(DiscountFactorItem.SEMI_ANNUAL, new BigDecimal(112.123445));
        listOfDiscountFactorItem.add(discountFactorMap);
        OrganizationGeneralInformation organizationGeneralInformation = OrganizationGeneralInformation.createOrganizationGeneralInformation("OI001");
        organizationGeneralInformation.withDiscountFactorOrganizationInformation(listOfDiscountFactorItem);

        assertNotNull(organizationGeneralInformation.getDiscountFactorItems());
        assertThat(3,is(organizationGeneralInformation.getDiscountFactorItems().size()));

    }

    @Test
    public void givenModalFactorOrganizationInformation_thenItShouldAddTheEntryToTheOrganizationInformation(){
        List<Map<ModalFactorItem,BigDecimal>> listOfModalFactorItem  = Lists.newArrayList();
        Map<ModalFactorItem,BigDecimal> modalFactorMap = Maps.newLinkedHashMap();
        modalFactorMap.put(ModalFactorItem.SEMI_ANNUAL, new BigDecimal(100.188446));
        listOfModalFactorItem.add(modalFactorMap);

        modalFactorMap = Maps.newLinkedHashMap();
        modalFactorMap.put(ModalFactorItem.QUARTERLY, new BigDecimal(101.123448));
        listOfModalFactorItem.add(modalFactorMap);

        modalFactorMap = Maps.newLinkedHashMap();
        modalFactorMap.put(ModalFactorItem.MONTHLY, new BigDecimal(102.123449));
        listOfModalFactorItem.add(modalFactorMap);

        OrganizationGeneralInformation organizationGeneralInformation = OrganizationGeneralInformation.createOrganizationGeneralInformation("OI001");
        organizationGeneralInformation.withModalFactorOrganizationInformation(listOfModalFactorItem);

        assertNotNull((organizationGeneralInformation.getModelFactorItems()));
        assertThat(3,is(organizationGeneralInformation.getModelFactorItems().size()));
    }
}
