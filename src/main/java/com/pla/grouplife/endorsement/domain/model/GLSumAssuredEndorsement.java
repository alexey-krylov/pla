package com.pla.grouplife.endorsement.domain.model;

import com.pla.sharedkernel.domain.model.FamilyId;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * Created by Samir on 8/4/2015.
 */
@Getter
public class GLSumAssuredEndorsement {

    private BigDecimal sumAssured;

    private FamilyId familyId;


    public GLSumAssuredEndorsement(BigDecimal sumAssured, FamilyId familyId) {
        this.familyId = familyId;
    }
}
