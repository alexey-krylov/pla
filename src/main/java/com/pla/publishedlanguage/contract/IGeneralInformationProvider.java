package com.pla.publishedlanguage.contract;

import com.pla.publishedlanguage.dto.AgentLoadingFactorDto;
import com.pla.sharedkernel.identifier.LineOfBusinessEnum;

/**
 * Created by Samir on 6/23/2015.
 */
public interface IGeneralInformationProvider {

    AgentLoadingFactorDto getAgeLoadingFactor(LineOfBusinessEnum lineOfBusinessEnum);
}
