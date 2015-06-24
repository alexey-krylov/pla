package com.pla.core.domain.service;

import com.pla.core.domain.model.generalinformation.AgentLoadingFactor;
import com.pla.core.domain.model.generalinformation.ProductLineGeneralInformation;
import com.pla.publishedlanguage.contract.IGeneralInformationProvider;
import com.pla.publishedlanguage.dto.AgentLoadingFactorDto;
import com.pla.sharedkernel.identifier.LineOfBusinessEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Created by Samir on 6/23/2015.
 */
@Service(value = "generalInformationProvider")
public class GeneralInformationProviderImpl implements IGeneralInformationProvider {

    private GeneralInformationService generalInformationService;


    @Autowired
    public GeneralInformationProviderImpl(GeneralInformationService generalInformationService) {
        this.generalInformationService = generalInformationService;
    }

    @Override
    public AgentLoadingFactorDto getAgeLoadingFactor(LineOfBusinessEnum lineOfBusinessEnum) {
        ProductLineGeneralInformation productLineGeneralInformation = generalInformationService.findProductLineInformationByLineOfBusinessId(lineOfBusinessEnum);
        checkArgument(productLineGeneralInformation != null, "Configure product line general information");
        AgentLoadingFactor ageLoadingFactor = productLineGeneralInformation.getAgeLoadingFactor();
        return ageLoadingFactor != null ? new AgentLoadingFactorDto(ageLoadingFactor.getAge(), ageLoadingFactor.getLoadingFactor()) : new AgentLoadingFactorDto(0, BigDecimal.ZERO);
    }
}
