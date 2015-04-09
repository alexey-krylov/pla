package com.pla.core.presentation.dto;

import com.google.common.collect.Sets;
import com.pla.core.dto.CommissionTermDto;
import com.pla.core.query.PlanFinder;
import com.pla.sharedkernel.domain.model.CommissionTermType;
import com.pla.sharedkernel.domain.model.CommissionType;
import com.pla.sharedkernel.identifier.PlanId;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Created by User on 4/9/2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class CommissionDtoTest {


    private PlanFinder planFinder;

    @Mock
    private MongoTemplate mongoTemplate;

    @Before
    public void setUp() {
        this.planFinder = new PlanFinder(this.mongoTemplate);
    }

    @Test
    public void shouldReturnCommissionDtosWhenGivenListOfAllCommissionsAndCommissionTerms() {
        List<Map<String, Object>> commissions = new ArrayList();
        Map<String, Object> commissionMap = new LinkedHashMap<>();
        commissionMap.put("planId", "1234");
        commissionMap.put("commissionId", "123123-SAFS");
        commissionMap.put("fromDate", "2014-03-04");
        commissionMap.put("toDate", "2014-03-07");
        commissionMap.put("availableFor", "REGIONAL_MANAGER");
        commissionMap.put("commissionTermType", "RANGE");
        commissionMap.put("commissionType", "OVERRIDE");
        commissions.add(commissionMap);

        List<Map<String, Object>> allCommissionTerms = new ArrayList();
        Map<String, Object> commissionTermMap = new LinkedHashMap<>();
        commissionTermMap.put("commissionId", "123123-SAFS");
        commissionTermMap.put("startYear", 1);
        commissionTermMap.put("endYear", 3);
        commissionTermMap.put("commissionPercentage", new BigDecimal(Double.valueOf("34.90")));
        allCommissionTerms.add(commissionTermMap);

        Map<String, Map<String, Object>> planDetails = new LinkedHashMap<>();
        Map<String, Object> planName = new LinkedHashMap<>();
        planName.put("planName", "Plan1");
        planDetails.put("planDetail", planName);
        when(planFinder.findPlanByPlanId(new PlanId("1234"))).thenReturn(planDetails);

        CommissionDto commissionDto = new CommissionDto();
        commissionDto.setPlanId("1234");
        commissionDto.setFromDate(new LocalDate("2014-03-04"));
        commissionDto.setPlanName("Plan1");
        commissionDto.setCommissionId("123123-SAFS");
        commissionDto.setCommissionType(CommissionType.OVERRIDE);
        commissionDto.setTermType(CommissionTermType.RANGE);
        Set<CommissionTermDto> commissionTermDtos = Sets.newHashSet();
        CommissionTermDto commissionTermDto = new CommissionTermDto();
        commissionTermDto.setStartYear(1);
        commissionTermDto.setEndYear(3);
        commissionTermDto.setCommissionPercentage(new BigDecimal(34.90));
        commissionTermDtos.add(commissionTermDto);
        commissionDto.setCommissionTermSet(commissionTermDtos);

        List<CommissionDto> commissionDtos = new ArrayList<>();
        commissionDtos.add(commissionDto);

        assertEquals(CommissionDto.transformToCommissionDto(commissions, allCommissionTerms, planFinder).get(0).toString(), commissionDtos.get(0).toString());


    }

}
