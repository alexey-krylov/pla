package com.pla.individuallife.endorsement.domain.model;

import com.pla.sharedkernel.domain.model.FamilyId;
import lombok.Getter;

/**
 * Created by Samir on 8/4/2015.
 */
@Getter
public class ILAssuredMANNumberEndorsement {

    private FamilyId familyId;

    private String manNumber;

    public ILAssuredMANNumberEndorsement(FamilyId familyId, String manNumber) {
        this.familyId = familyId;
        this.manNumber = manNumber;
    }
}
