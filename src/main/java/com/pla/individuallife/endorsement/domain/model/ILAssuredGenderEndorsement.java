package com.pla.individuallife.endorsement.domain.model;

import com.pla.sharedkernel.domain.model.FamilyId;
import com.pla.sharedkernel.domain.model.Gender;
import lombok.Getter;

/**
 * Created by Samir on 8/4/2015.
 */
@Getter
public class ILAssuredGenderEndorsement {

    private FamilyId familyId;

    private Gender gender;

    public ILAssuredGenderEndorsement(FamilyId familyId, Gender gender) {
        this.familyId = familyId;
        this.gender = gender;
    }
}
