package com.pla.grouplife.endorsement.domain.model;

import com.pla.grouplife.sharedresource.model.vo.Insured;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

/**
 * Created by Samir on 8/4/2015.
 */
@Setter
@Getter
public class GLMemberEndorsement {

    private Set<Insured> insureds;

    public GLMemberEndorsement(Set<Insured> insureds) {
        this.insureds = insureds;
    }


}
