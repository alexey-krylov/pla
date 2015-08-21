package com.pla.core.presentation.dto;

import com.google.common.collect.Sets;
import com.pla.core.dto.CommissionTermDto;
import com.pla.core.query.PlanFinder;
import com.pla.sharedkernel.domain.model.CommissionDesignation;
import com.pla.sharedkernel.domain.model.CommissionTermType;
import com.pla.sharedkernel.domain.model.CommissionType;
import com.pla.sharedkernel.domain.model.PremiumFee;
import com.pla.sharedkernel.identifier.PlanId;
import org.hamcrest.CoreMatchers;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

/**
 * Created by User on 4/9/2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class CommissionDtoTest {

    @Mock
    private PlanFinder planFinder;


    @Test
    public void shouldReturnCommissionDtosWhenGivenListOfAllCommissionsAndCommissionTerms() {
        List<Map<String, Object>> commissions = new ArrayList();
        Map<String, Object> commissionMap = new LinkedHashMap<>();
        commissionMap.put("planId", "1234");
        commissionMap.put("commissionId", "123123sSAFS");
        commissionMap.put("fromDate", "2014-03-04");
        commissionMap.put("toDate", "2014-03-07");
        commissionMap.put("availableFor", "REGIONAL_MANAGER");
        commissionMap.put("commissionType", "OVERRIDE");
        commissionMap.put("premiumFee", "POLICY_FEE");
        commissions.add(commissionMap);

        List<Map<String, Object>> allCommissionTerms = new ArrayList();
        Map<String, Object> commissionTermMap = new LinkedHashMap<>();
        commissionTermMap.put("commissionId", "123123sSAFS");
        commissionTermMap.put("startYear", 1);
        commissionTermMap.put("endYear", 3);
        commissionTermMap.put("commissionPercentage", new BigDecimal(Double.valueOf("34.90")));
        commissionTermMap.put("commissionTermType", CommissionTermType.RANGE);
        allCommissionTerms.add(commissionTermMap);

        Map<String, Map<String, Object>> planDetails = new LinkedHashMap<>();
        Map<String, Object> planName = new LinkedHashMap<>();
        planName.put("planName", "Plan1");
        planDetails.put("planDetail", planName);

        when(planFinder.findPlanByPlanId(new PlanId("1234"))).thenReturn(planDetails);

        CommissionDto expectedCommissionDto = new CommissionDto();
        expectedCommissionDto.setPlanId("1234");
        expectedCommissionDto.setFromDate(new LocalDate("2014-03-04"));
        expectedCommissionDto.setPlanName("Plan1");
        expectedCommissionDto.setCommissionId("123123sSAFS");
        expectedCommissionDto.setCommissionType(CommissionType.OVERRIDE);
        expectedCommissionDto.setPremiumFee(PremiumFee.POLICY_FEE);
        expectedCommissionDto.setAvailableFor(CommissionDesignation.REGIONAL_MANAGER);
        Set<CommissionTermDto> commissionTermDtos = Sets.newHashSet();
        CommissionTermDto commissionTermDto = new CommissionTermDto();
        commissionTermDto.setStartYear(1);
        commissionTermDto.setEndYear(3);
        commissionTermDto.setCommissionTermType(CommissionTermType.RANGE);
        commissionTermDto.setCommissionPercentage(new BigDecimal(34.90));
        commissionTermDtos.add(commissionTermDto);
        expectedCommissionDto.setCommissionTermSet(commissionTermDtos);

        List<CommissionDto> transformedCommissionDtos = CommissionDto.transformToCommissionDto(commissions, allCommissionTerms, planFinder);

        assertThat(transformedCommissionDtos.get(0), CoreMatchers.is(expectedCommissionDto));


    }

    @Test
    public void shouldSetCommissionTermSetAndReturnCommissionDtoWhenGivenCommissionTermsForCommission() {
        Map<String, Object> commissionMap = new LinkedHashMap<>();
        commissionMap.put("planId", "1234");
        commissionMap.put("commissionId", "123123-SAFS");
        commissionMap.put("fromDate", "2014-03-04");
        commissionMap.put("toDate", "2014-03-07");
        commissionMap.put("availableFor", "REGIONAL_MANAGER");
        commissionMap.put("commissionType", "OVERRIDE");
        commissionMap.put("premiumFee", "POLICY_FEE");

        List<Map<String, Object>> allCommissionTerms = new ArrayList();
        Map<String, Object> commissionTermMap = new LinkedHashMap<>();
        commissionTermMap.put("commissionId", "123123-SAFS");
        commissionTermMap.put("startYear", 1);
        commissionTermMap.put("endYear", 3);
        commissionTermMap.put("commissionPercentage", new BigDecimal(Double.valueOf("34.90")));
        commissionTermMap.put("commissionTermType", CommissionTermType.RANGE);
        allCommissionTerms.add(commissionTermMap);

        Map<String, Map<String, Object>> planDetails = new LinkedHashMap<>();
        Map<String, Object> planName = new LinkedHashMap<>();
        planName.put("planName", "Plan1");
        planDetails.put("planDetail", planName);
        when(planFinder.findPlanByPlanId(new PlanId("1234"))).thenReturn(planDetails);


        CommissionDto expectedCommissionDto = new CommissionDto();
        expectedCommissionDto.setPlanId("1234");
        expectedCommissionDto.setFromDate(new LocalDate("2014-03-04"));
        expectedCommissionDto.setPlanName("Plan1");
        expectedCommissionDto.setCommissionId("123123-SAFS");
        expectedCommissionDto.setCommissionType(CommissionType.OVERRIDE);
        expectedCommissionDto.setPremiumFee(PremiumFee.POLICY_FEE);

        CommissionTermDto expectedCommissionTermDto = new CommissionTermDto();
        expectedCommissionTermDto.setStartYear(1);
        expectedCommissionTermDto.setEndYear(3);
        expectedCommissionTermDto.setCommissionPercentage(new BigDecimal(34.90));
        expectedCommissionTermDto.setCommissionTermType(CommissionTermType.RANGE);


        CommissionDto transformedCommissionDto = CommissionDto.transformToCommissionDto(commissionMap, allCommissionTerms, planFinder);

        //assertThat(transformedCommissionDto, CoreMatchers.is(expectedCommissionDto));
        assertThat(transformedCommissionDto.getCommissionTermSet(), CoreMatchers.hasItems(expectedCommissionTermDto));


    }


}
