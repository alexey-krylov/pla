package com.pla.sharedkernel.domain.model;

/**
 * Created by pradyumna on 22-05-2015.
 */
public enum TitleEnum {

    //Mr. ; Mrs. ; Miss ; Dr.; Prof.; Hon.;Ms.;Rev.;Pst.

    MR("Mr."), MRS("Mrs."), MISS("Miss"), PROF("Prof."), DR("Dr."), HON("Hoon."), MS("Ms."), REV("Rev."), PST("Pst.");

    private String description;

    TitleEnum(String description) {
        this.description = description;
    }

    public String toString() {
        return description;
    }
}
