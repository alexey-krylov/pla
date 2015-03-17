/*
 * Copyright (c) 3/16/15 9:46 AM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.sharedkernel.specification;

/**
 * @author: Samir
 * @since 1.0 16/03/2015
 */
public interface ICompositeSpecification<T> extends ISpecification<T>{

    ICompositeSpecification<T> And(ISpecification<T> other);

}
