package com.pla.sharedkernel.domain.model;

import lombok.Getter;

/**
 * Created by Samir on 7/8/2015.
 */
@Getter
public class FamilyId {

    private String familyId;

    public FamilyId(String familyId) {
        this.familyId = familyId;
    }
}
