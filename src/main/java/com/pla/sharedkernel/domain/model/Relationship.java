package com.pla.sharedkernel.domain.model;

import java.util.ArrayList;
import java.util.List;

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

    public static Relationship getRelationship(String description) {
        for (Relationship relationship : Relationship.values()) {
            if (description.equals(relationship.description)) {
                return relationship;
            }
        }
        return null;
    }

    public static List<String> getAllRelation() {
        List<String> allRelations = new ArrayList<>();
        for (Relationship relationship : Relationship.values()) {
            allRelations.add(relationship.description);
        }
        return allRelations;
    }
}
