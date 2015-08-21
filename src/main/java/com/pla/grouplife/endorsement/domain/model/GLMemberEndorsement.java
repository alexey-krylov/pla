package com.pla.grouplife.endorsement.domain.model;

import com.pla.grouplife.sharedresource.model.vo.Insured;
import lombok.Getter;

import java.util.List;

/**
 * Created by Samir on 8/4/2015.
 */
@Getter
public class GLMemberEndorsement {

    private List<Insured> insureds;

    public GLMemberEndorsement(List<Insured> insureds) {
        this.insureds = insureds;
    }
}
