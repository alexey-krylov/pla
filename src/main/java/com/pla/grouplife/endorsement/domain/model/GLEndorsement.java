package com.pla.grouplife.endorsement.domain.model;

import com.pla.sharedkernel.domain.model.EndorsementType;
import lombok.Getter;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import java.util.List;

/**
 * Created by Samir on 8/3/2015.
 */
@Getter
@ValueObject
public class GLEndorsement {

    private EndorsementType endorsementType;

    private GLMemberEndorsement memberEndorsement;

    private GLSumAssuredEndorsement sumAssuredEndorsement;

    private GLPolicyHolderDetailEndorsement policyHolderDetailEndorsement;

    private GLAssuredNameEndorsement assuredNameEndorsement;

    private GLPremiumEndorsement premiumEndorsement;

    private List<GLAssuredDOBEndorsement> dobEndorsements;

    private List<GLAssuredGenderEndorsement> genderEndorsements;

    private List<GLAssuredNRCEndorsement> nrcEndorsements;

    private List<GLAssuredMANNumberEndorsement> manNumberEndorsements;

    public GLEndorsement(EndorsementType endorsementType) {
        this.endorsementType = endorsementType;
    }

    public GLEndorsement addMemberEndorsement(GLMemberEndorsement memberEndorsement) {
        this.memberEndorsement = memberEndorsement;
        return this;
    }

    public GLEndorsement addSAEndorsement(GLSumAssuredEndorsement sumAssuredEndorsement) {
        this.sumAssuredEndorsement = sumAssuredEndorsement;
        return this;
    }

    public GLEndorsement addPolicyHolderDetailEndorsement(GLPolicyHolderDetailEndorsement policyHolderDetailEndorsement) {
        this.policyHolderDetailEndorsement = policyHolderDetailEndorsement;
        return this;
    }

    public GLEndorsement addAssuredNameEndorsement(GLAssuredNameEndorsement glAssuredNameEndorsement) {
        this.assuredNameEndorsement = glAssuredNameEndorsement;
        return this;
    }

    public GLEndorsement addPremiumEndorsement(GLPremiumEndorsement glPremiumEndorsement) {
        this.premiumEndorsement = glPremiumEndorsement;
        return this;
    }

    public GLEndorsement addDOBEndorsement(List<GLAssuredDOBEndorsement> glAssuredDOBEndorsements) {
        this.dobEndorsements = glAssuredDOBEndorsements;
        return this;
    }

    public GLEndorsement addGenderEndorsement(List<GLAssuredGenderEndorsement> genderEndorsements) {
        this.genderEndorsements = genderEndorsements;
        return this;
    }

    public GLEndorsement addNrcEndorsement(List<GLAssuredNRCEndorsement> nrcEndorsements) {
        this.nrcEndorsements = nrcEndorsements;
        return this;
    }

    public GLEndorsement addManNumberEndorsement(List<GLAssuredMANNumberEndorsement> manNumberEndorsements) {
        this.manNumberEndorsements = manNumberEndorsements;
        return this;
    }
}
