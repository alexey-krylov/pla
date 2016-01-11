package com.pla.grouplife.endorsement.domain.model;

import com.pla.grouplife.sharedresource.model.vo.GLEndorsementInsured;
import lombok.Getter;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import java.util.List;

/**
 * Created by Samir on 8/3/2015.
 */
@Getter
@ValueObject
public class GLEndorsement {

    private GLMemberEndorsement memberEndorsement;

    private GLSumAssuredEndorsement sumAssuredEndorsement;

    private GLPolicyHolderDetailEndorsement policyHolderDetailEndorsement;

    private GLAssuredNameEndorsement assuredNameEndorsement;

    private GLMemberEndorsement premiumEndorsement;

    private GLMemberEndorsement memberDeletionEndorsements;

    private List<GLAssuredDOBEndorsement> dobEndorsements;

    private List<GLAssuredGenderEndorsement> genderEndorsements;

    private List<GLAssuredNRCEndorsement> nrcEndorsements;

    private List<GLAssuredMANNumberEndorsement> manNumberEndorsements;

    private GLMemberEndorsement newCategoryRelationEndorsement;

    private GLEndorsementInsured freeCoverLimitEndorsement;

    public GLEndorsement addMemberEndorsement(GLMemberEndorsement memberEndorsement) {
        this.memberEndorsement = memberEndorsement;
        return this;
    }

    public GLEndorsement addNewCategoryRelationEndorsement(GLMemberEndorsement memberEndorsement) {
        this.newCategoryRelationEndorsement = memberEndorsement;
        return this;
    }

    public GLEndorsement addSAEndorsement(GLSumAssuredEndorsement sumAssuredEndorsement) {
        this.sumAssuredEndorsement = sumAssuredEndorsement;
        return this;
    }

    public GLEndorsement addMemberDeletionEndorsement(GLMemberEndorsement glMemberDeletionEndorsements) {
        this.memberDeletionEndorsements = glMemberDeletionEndorsements;
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

    public GLEndorsement addPremiumEndorsement(GLMemberEndorsement glPremiumEndorsement) {
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


    public GLEndorsement createFCLEndorsement(GLEndorsementInsured freeCoverLimitEndorsement) {
        this.freeCoverLimitEndorsement = freeCoverLimitEndorsement;
        return this;
    }

    public GLEndorsement updateWithFCLEndorsement(GLEndorsementInsured glEndorsementInsured) {
        this.freeCoverLimitEndorsement = freeCoverLimitEndorsement;
        return this;
    }
}
