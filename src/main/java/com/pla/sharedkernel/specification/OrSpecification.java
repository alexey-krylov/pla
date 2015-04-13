package com.pla.sharedkernel.specification;

/**
 * Created by Admin on 4/13/2015.
 */
public class OrSpecification<T> extends CompositeSpecification<T> {

    private ISpecification<T> left;

    private ISpecification<T> right;

    public OrSpecification(ISpecification<T> left, ISpecification<T> right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean isSatisfiedBy(T candidate) {
        return left.isSatisfiedBy(candidate) && right.isSatisfiedBy(candidate);
    }
}
