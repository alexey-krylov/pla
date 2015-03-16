/*
 * Copyright (c) 3/14/15 9:56 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.sharedkernel.specification;

/**
 * @author: Samir
 * @since 1.0 14/03/2015
 */
public class AndSpecification<T> extends CompositeSpecification<T> {

    private ISpecification<T> left;

    private ISpecification<T> right;

    public AndSpecification(ISpecification<T> left, ISpecification<T> right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean isSatisfiedBy(T candidate) {
        return left.isSatisfiedBy(candidate) && right.isSatisfiedBy(candidate);
    }
}
