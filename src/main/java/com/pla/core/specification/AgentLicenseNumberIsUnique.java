/*
 * Copyright (c) 3/16/15 6:12 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.specification;

import com.pla.core.domain.model.agent.LicenseNumber;
import com.pla.core.query.AgentFinder;
import com.pla.sharedkernel.specification.ISpecification;
import org.nthdimenzion.ddd.domain.annotations.Specification;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author: Samir
 * @since 1.0 16/03/2015
 */
@Specification
public class AgentLicenseNumberIsUnique implements ISpecification<LicenseNumber> {

    private AgentFinder agentFinder;

    @Autowired
    public AgentLicenseNumberIsUnique(AgentFinder agentFinder) {
        this.agentFinder = agentFinder;
    }

    @Override
    public boolean isSatisfiedBy(LicenseNumber licenseNumber) {
        int noOfAgent = agentFinder.getAgentCountByLicenseNumber(licenseNumber.getLicenseNumber());
        return noOfAgent == 0;
    }
}
