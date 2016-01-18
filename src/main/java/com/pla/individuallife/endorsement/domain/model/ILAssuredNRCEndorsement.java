package com.pla.individuallife.endorsement.domain.model;

import com.pla.sharedkernel.domain.model.FamilyId;
import lombok.Getter;

/**
 * Created by Samir on 8/4/2015.
 */
@Getter
public class ILAssuredNRCEndorsement {

    private FamilyId familyId;

    private String nrcNumber;

    public ILAssuredNRCEndorsement(FamilyId familyId, String nrcNumber) {
        this.familyId = familyId;
        this.nrcNumber = nrcNumber;
    }
}
