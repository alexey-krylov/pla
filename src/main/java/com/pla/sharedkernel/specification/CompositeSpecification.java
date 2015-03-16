/*
 * Copyright (c) 3/14/15 9:09 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.sharedkernel.specification;

/**
 * @author: Samir
 * @since 1.0 14/03/2015
 */
public abstract class CompositeSpecification<T> implements ISpecification<T> {

    public abstract boolean isSatisfiedBy(T candidate);

    public ISpecification<T> and(ISpecification<T> other) {
        return new AndSpecification<T>(this, other);
    }

}
