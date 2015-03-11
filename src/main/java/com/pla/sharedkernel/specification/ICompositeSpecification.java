/*
 * Copyright (c) 3/11/15 9:58 AM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.sharedkernel.specification;

/**
 * @author: Samir
 * @since 1.0 11/03/2015
 */
public interface ICompositeSpecification<T1,T2> {

    boolean isSatisfiedBy(T1 object1, T2 object2);

    boolean isGeneralizationOf(ISpecification<T2> specification,T2 data);
}
