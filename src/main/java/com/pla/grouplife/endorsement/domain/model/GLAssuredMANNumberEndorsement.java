package com.pla.grouplife.endorsement.domain.model;

import com.pla.sharedkernel.domain.model.FamilyId;
import lombok.Getter;

/**
 * Created by Samir on 8/4/2015.
 */
@Getter
public class GLAssuredMANNumberEndorsement {

    private FamilyId familyId;

    private String manNumber;

    public GLAssuredMANNumberEndorsement(FamilyId familyId, String manNumber) {
        this.familyId = familyId;
        this.manNumber = manNumber;
    }
}
