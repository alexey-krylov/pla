/*
 * Copyright (c) 3/5/15 3:44 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.sharedkernel.specification;

/**
 * @author: Samir
 * @since 1.0 05/03/2015
 */
public interface ISpecification<T> {

    boolean isSatisfiedBy(T data);

}
