package com.pla.individuallife.endorsement.domain.model;

import com.pla.sharedkernel.domain.model.FamilyId;
import lombok.Getter;
import org.joda.time.LocalDate;

/**
 * Created by Samir on 8/4/2015.
 */
@Getter
public class ILAssuredDOBEndorsement {

    private FamilyId familyId;

    private LocalDate dateOfBirth;

    public ILAssuredDOBEndorsement(FamilyId familyId, LocalDate dateOfBirth) {
        this.familyId = familyId;
        this.dateOfBirth = dateOfBirth;
    }
}
