/*
 * Copyright (c) 3/9/15 4:33 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.specification;

import com.pla.sharedkernel.specification.ISpecification;
import org.nthdimenzion.ddd.domain.annotations.Specification;

/**
 * @author: Samir
 * @since 1.0 09/03/2015
 */
@Specification
public class BenefitIsUpdatable implements ISpecification<String>{
    
    @Override
    public boolean isSatisfiedBy(String data) {
        return false;
    }
}
