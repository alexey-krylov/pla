package com.pla.core.domain.service;

import com.pla.core.domain.model.generalinformation.ProductLineGeneralInformation;
import com.pla.publishedlanguage.dto.AgentLoadingFactorDto;
import com.pla.sharedkernel.exception.ProcessInfoException;
import com.pla.sharedkernel.identifier.LineOfBusinessEnum;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;

/**
 * Created by Admin on 6/24/2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class GeneralInformationProviderImplUnitTest {

    @Mock
    private GeneralInformationService generalInformationService;


    @Test
    public void givenLineOfBusinessId_thenItShouldReturnTheAgentLoadingFactorDefinedForTheProductLine() throws ProcessInfoException {
        ProductLineGeneralInformation productLineGeneralInformation = ProductLineGeneralInformation.createProductLineGeneralInformation(LineOfBusinessEnum.GROUP_HEALTH);
        productLineGeneralInformation.withAgeLoadingFactor(20,new BigDecimal(58.99));
        when(generalInformationService.findProductLineInformationByLineOfBusinessId(LineOfBusinessEnum.GROUP_HEALTH)).thenReturn(productLineGeneralInformation);
        GeneralInformationProviderImpl generalInformationProvider = new GeneralInformationProviderImpl(generalInformationService);
        AgentLoadingFactorDto agentLoadingFactorDto =  generalInformationProvider.getAgeLoadingFactor(LineOfBusinessEnum.GROUP_HEALTH);
        assertThat(agentLoadingFactorDto.getAge(),is(20));
        assertThat(agentLoadingFactorDto.getLoadingFactor(),is(new BigDecimal(58.99)));
    }

    @Test
    public void givenLineOfBusinessIdAsGroupLife_whenAgentLoadingFactorIsNotDefinedForTheProductLine_thenItShouldReturnTheAgentLoadingWithZeroFactor() throws ProcessInfoException {
        ProductLineGeneralInformation productLineGeneralInformation = ProductLineGeneralInformation.createProductLineGeneralInformation(LineOfBusinessEnum.GROUP_LIFE);
        when(generalInformationService.findProductLineInformationByLineOfBusinessId(LineOfBusinessEnum.GROUP_LIFE)).thenReturn(productLineGeneralInformation);
        GeneralInformationProviderImpl generalInformationProvider = new GeneralInformationProviderImpl(generalInformationService);
        AgentLoadingFactorDto agentLoadingFactorDto =  generalInformationProvider.getAgeLoadingFactor(LineOfBusinessEnum.GROUP_LIFE);
        assertThat(agentLoadingFactorDto.getAge(),is(0));
        assertThat(agentLoadingFactorDto.getLoadingFactor(),is(new BigDecimal(0)));
    }
}
