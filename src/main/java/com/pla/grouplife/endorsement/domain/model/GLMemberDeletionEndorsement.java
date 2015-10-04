package com.pla.grouplife.endorsement.domain.model;

import com.pla.sharedkernel.domain.model.FamilyId;
import com.pla.sharedkernel.domain.model.Relationship;
import lombok.Getter;

/**
 * Created by Samir on 10/4/2015.
 */
@Getter
public class GLMemberDeletionEndorsement {

    private String category;

    private Relationship relationship;

    private Integer noOfAssured;

    private FamilyId familyId;

    public GLMemberDeletionEndorsement(String category, Relationship relationship, Integer noOfAssured, FamilyId familyId) {
        this.category = category;
        this.relationship = relationship;
        this.noOfAssured = noOfAssured;
        this.familyId = familyId;
    }
}
