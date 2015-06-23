package com.pla.core.domain.service;

import com.pla.publishedlanguage.contract.IGeneralInformationProvider;
import com.pla.publishedlanguage.dto.AgentLoadingFactorDto;
import com.pla.sharedkernel.identifier.LineOfBusinessEnum;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Created by Samir on 6/23/2015.
 */
@Service(value = "generalInformationProvider")
public class GeneralInformationProviderImpl implements IGeneralInformationProvider {
    @Override
    public AgentLoadingFactorDto getAgeLoadingFactor(LineOfBusinessEnum lineOfBusinessEnum) {
        return new AgentLoadingFactorDto(0, BigDecimal.ZERO);
    }
}
