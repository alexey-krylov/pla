package com.pla.grouplife.endorsement.domain.model;

import com.pla.sharedkernel.domain.model.FamilyId;
import lombok.Getter;
import org.joda.time.LocalDate;

/**
 * Created by Samir on 8/4/2015.
 */
@Getter
public class GLAssuredDOBEndorsement {

    private FamilyId familyId;

    private LocalDate dateOfBirth;

    public GLAssuredDOBEndorsement(FamilyId familyId, LocalDate dateOfBirth) {
        this.familyId = familyId;
        this.dateOfBirth = dateOfBirth;
    }
}
