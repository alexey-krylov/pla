package com.pla.core.domain.query;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.pla.core.dto.CoverageDto;
import com.pla.core.query.CoverageFinder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Created by Admin on 3/26/2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:queryTestContext.xml"})
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class})
public class CoverageFinderUnitTest {

    CoverageFinder coverageFinder;

    @Autowired
    ApplicationContext ctx;

    @Before
    public void setUp(){
        coverageFinder = ctx.getAutowireCapableBeanFactory().createBean(CoverageFinder.class);
    }

    @Test
    @DatabaseSetup(value = "classpath:testdata/endtoend/coverage/testdataforcoverage.xml")
    public void getAllTheActiveCoverageAndTheSetOfActiveBenefitsAssociatedWithTheCoverage() {
        List<CoverageDto> coverageDtos = new ArrayList<>();

        CoverageDto coverageDto = new CoverageDto();
        coverageDto.setCoverageId("C001");
        coverageDto.setCoverageName("coverage name One");
        coverageDto.setDescription("description one");
        coverageDto.setCoverageStatus("ACTIVE");

        List<Map<String,Object>> listOfBenefit = new ArrayList<>();

        Map<String,Object> benefitMap = new LinkedHashMap<>();
        benefitMap.put("benefitId", "B001");
        benefitMap.put("benefitName", "benefit name one");
        listOfBenefit.add(benefitMap);


        benefitMap = new LinkedHashMap<>();
        benefitMap.put("benefitId","B002");
        benefitMap.put("benefitName","benefit name two");
        listOfBenefit.add(benefitMap);

        coverageDto.setBenefitDtos(listOfBenefit);
        coverageDtos.add(coverageDto);

        List<CoverageDto> listOfActiveCoverage = coverageFinder.getAllCoverage();
        assertThat(4, is(listOfActiveCoverage.size()));
        assertThat(2, is(listOfActiveCoverage.get(0).getBenefitDtos().size()));
        assertThat("benefit name one", is(listOfActiveCoverage.get(0).getBenefitDtos().get(0).get("benefitName")));

    }

    @Test
    @DatabaseSetup(value = "classpath:testdata/endtoend/coverage/testdataforcoverage.xml")
    public void givenTheCoverageName_whenTheCoverageNameIsUnique_thenItShouldReturnZeroCount() {
        int coverageCountByCoverageName = coverageFinder.getCoverageCountByCoverageName("new coverage name");
        assertThat(0, is(coverageCountByCoverageName));

    }

    @Test
    @DatabaseSetup(value = "classpath:testdata/endtoend/coverage/testdataforcoverage.xml")
    public void givenTheCoverageName_whenTheCoverageNameIsNotUnique_thenItShouldReturnCountAsGreaterThanZero() {
        int coverageCountByCoverageName = coverageFinder.getCoverageCountByCoverageName("testing Coverage one");
        assertThat(1, is(coverageCountByCoverageName));

    }
}
