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
public class AndSpecification<S, O> implements ICompositeSpecification<S, O> {

    private ISpecification<S> left;

    private ISpecification<O> right;

    public AndSpecification(ISpecification<S> left, ISpecification<O> right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean isSatisfiedBy(S leftCandidate, O rightCandidate) {
        return left.isSatisfiedBy(leftCandidate) && right.isSatisfiedBy(rightCandidate);
    }
}
