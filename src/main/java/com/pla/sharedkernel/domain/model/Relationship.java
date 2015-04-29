package com.pla.sharedkernel.domain.model;

/**
 * @author: pradyumna
 * @since 1.0 12/03/2015
 */
public enum Relationship {

    SELF("Self"), SISTER("Sister"), BROTHER("Brother"), SPOUSE("Spouse"), FATHER("Father"), MOTHER("Mother"), SON("Son"), DAUGHTER("Daughter"), FATHER_IN_LAW("Father In Law"), MOTHER_IN_LAW("Mother In Law"), DEPENDENTS("Dependents"),
    STEP_SON("Step Son"), STEP_DAUGHTER("Step Daughter");

    public String description;

    Relationship(String description) {
        this.description = description;
    }
}
