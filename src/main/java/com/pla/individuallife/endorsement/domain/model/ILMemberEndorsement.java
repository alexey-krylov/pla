package com.pla.individuallife.endorsement.domain.model;

import com.pla.grouplife.sharedresource.model.vo.Insured;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

/**
 * Created by Samir on 8/4/2015.
 */
@Setter
@Getter
public class ILMemberEndorsement {

    private Set<Insured> insureds;

    public ILMemberEndorsement(Set<Insured> insureds) {
        this.insureds = insureds;
    }


}
