package com.pla.grouplife.claim.presentation.dto;

import com.pla.grouplife.sharedresource.model.vo.Insured;
import com.pla.grouplife.sharedresource.model.vo.InsuredDependent;
import com.pla.grouplife.sharedresource.model.vo.Proposer;
import com.pla.sharedkernel.domain.model.ClaimType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by Admin on 9/21/2015.
 */
@Getter
@Setter
public class ClaimIntimationDetailDto {
    private Proposer proposer;
    private AssuredDetailDto assuredDetailDto;
    private List<ClaimType> claimTypes;

    public ClaimIntimationDetailDto withProposer(Proposer proposer){
        this.proposer  = proposer;
        return this;
    }

    public ClaimIntimationDetailDto withInsuredAssuredDetail(Insured insured){
        this.assuredDetailDto = AssuredDetailDto.getInstance(insured);
        return this;
    }

    public ClaimIntimationDetailDto withInsuredDependentAssuredDetail(InsuredDependent insuredDependent){
        this.assuredDetailDto = AssuredDetailDto.getInstance(insuredDependent);
        return this;
    }
}
