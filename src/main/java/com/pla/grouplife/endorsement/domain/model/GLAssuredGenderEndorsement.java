package com.pla.grouplife.endorsement.domain.model;

import com.pla.sharedkernel.domain.model.FamilyId;
import com.pla.sharedkernel.domain.model.Gender;
import lombok.Getter;

/**
 * Created by Samir on 8/4/2015.
 */
@Getter
public class GLAssuredGenderEndorsement {

    private FamilyId familyId;

    private Gender gender;

    public GLAssuredGenderEndorsement(FamilyId familyId, Gender gender) {
        this.familyId = familyId;
        this.gender = gender;
    }
}
