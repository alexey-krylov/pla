package com.pla.individuallife.endorsement.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import java.util.List;

/**
 * Created by Raghu on 8/3/2015.
 */
@Getter
@Setter
@ValueObject
@NoArgsConstructor
@AllArgsConstructor
public class ILEndorsement {

    private ILMemberEndorsement memberEndorsement;

    private ILSumAssuredEndorsement sumAssuredEndorsement;

    private ILPolicyHolderDetailEndorsement policyHolderDetailEndorsement;

    private ILAssuredNameEndorsement assuredNameEndorsement;

    private ILMemberEndorsement premiumEndorsement;

    private ILMemberEndorsement memberDeletionEndorsements;

    private List<ILAssuredDOBEndorsement> dobEndorsements;

    private List<ILAssuredGenderEndorsement> genderEndorsements;

    private List<ILAssuredNRCEndorsement> nrcEndorsements;

    private List<ILAssuredMANNumberEndorsement> manNumberEndorsements;

    private ILMemberEndorsement newCategoryRelationEndorsement;

    public ILEndorsement addMemberEndorsement(ILMemberEndorsement memberEndorsement) {
        this.memberEndorsement = memberEndorsement;
        return this;
    }

    public ILEndorsement addNewCategoryRelationEndorsement(ILMemberEndorsement memberEndorsement) {
        this.newCategoryRelationEndorsement = memberEndorsement;
        return this;
    }

    public ILEndorsement addSAEndorsement(ILSumAssuredEndorsement sumAssuredEndorsement) {
        this.sumAssuredEndorsement = sumAssuredEndorsement;
        return this;
    }

    public ILEndorsement addMemberDeletionEndorsement(ILMemberEndorsement glMemberDeletionEndorsements) {
        this.memberDeletionEndorsements = glMemberDeletionEndorsements;
        return this;
    }

    public ILEndorsement addPolicyHolderDetailEndorsement(ILPolicyHolderDetailEndorsement policyHolderDetailEndorsement) {
        this.policyHolderDetailEndorsement = policyHolderDetailEndorsement;
        return this;
    }

    public ILEndorsement addAssuredNameEndorsement(ILAssuredNameEndorsement ilAssuredNameEndorsement) {
        this.assuredNameEndorsement = ilAssuredNameEndorsement;
        return this;
    }

    public ILEndorsement addPremiumEndorsement(ILMemberEndorsement glPremiumEndorsement) {
        this.premiumEndorsement = glPremiumEndorsement;
        return this;
    }

    public ILEndorsement addDOBEndorsement(List<ILAssuredDOBEndorsement> ILAssuredDOBEndorsements) {
        this.dobEndorsements = ILAssuredDOBEndorsements;
        return this;
    }

    public ILEndorsement addGenderEndorsement(List<ILAssuredGenderEndorsement> genderEndorsements) {
        this.genderEndorsements = genderEndorsements;
        return this;
    }

    public ILEndorsement addNrcEndorsement(List<ILAssuredNRCEndorsement> nrcEndorsements) {
        this.nrcEndorsements = nrcEndorsements;
        return this;
    }

    public ILEndorsement addManNumberEndorsement(List<ILAssuredMANNumberEndorsement> manNumberEndorsements) {
        this.manNumberEndorsements = manNumberEndorsements;
        return this;
    }
}
