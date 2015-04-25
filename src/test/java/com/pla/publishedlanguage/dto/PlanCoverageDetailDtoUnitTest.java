package com.pla.publishedlanguage.dto;

import com.google.common.collect.Lists;
import com.pla.sharedkernel.identifier.CoverageId;
import com.pla.sharedkernel.identifier.PlanId;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Created by Admin on 4/23/2015.
 */
public class PlanCoverageDetailDtoUnitTest {

    private PlanCoverageDetailDto.SumAssuredDto sumAssuredDto;
    private PlanCoverageDetailDto planCoverageDetailDto;
    private PlanCoverageDetailDto.CoverageDto coverageDto;
    private List<PlanCoverageDetailDto.CoverageDto> coverageDtos;

    @Before
    public void setUp(){
        planCoverageDetailDto = new PlanCoverageDetailDto(new PlanId("P001"),"Premium Plan","P_CODE");
        sumAssuredDto = planCoverageDetailDto.new SumAssuredDto(BigDecimal.ONE,BigDecimal.TEN,12);
        coverageDto =  planCoverageDetailDto.new CoverageDto("C001","Insurance",new CoverageId("C_ONE"));
        coverageDtos = Lists.newArrayList();
        coverageDtos.add(coverageDto);
    }

    @Test
    public void givenListOfCoverage_thenItShouldAddAndReturnThePlanCoverageDetail(){
        PlanCoverageDetailDto planWithCoverageDetail =  planCoverageDetailDto.addCoverage(Arrays.asList(coverageDto));
        assertThat(coverageDtos, is(planWithCoverageDetail.getCoverageDtoList()));
        assertThat(new PlanId("P001"),is(planWithCoverageDetail.getPlanId()));
    }


    @Test
    public void givenSumAssured_thenItShouldAddAndReturnThePlanSumAssuredDetail(){
        PlanCoverageDetailDto planWithSumAssuredDetail =  planCoverageDetailDto.addSumAssured(sumAssuredDto);
        assertThat(sumAssuredDto, is(planWithSumAssuredDetail.getSumAssuredDto()));
    }

    @Test
    public void givenTheListOfRelationType_thenItShouldAddAndReturnThePlanDetailWithListOfRelationTypes(){
        List<String> relationTypes = Lists.newArrayList("SON","FATHER");
        PlanCoverageDetailDto planWithSumAssuredDetail =  planCoverageDetailDto.addRelationTypes(relationTypes);
        assertThat(relationTypes, is(planWithSumAssuredDetail.getRelationTypes()));
    }


}
