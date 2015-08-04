package com.pla.grouplife.endorsement.domain.model;

import com.pla.sharedkernel.domain.model.FamilyId;
import lombok.Getter;

/**
 * Created by Samir on 8/4/2015.
 */
@Getter
public class GLAssuredNRCEndorsement {

    private FamilyId familyId;

    private String nrcNumber;

    public GLAssuredNRCEndorsement(FamilyId familyId, String nrcNumber) {
        this.familyId = familyId;
        this.nrcNumber = nrcNumber;
    }
}
