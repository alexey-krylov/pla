package com.pla.grouplife.claim.presentation.dto;

import com.pla.grouplife.claim.domain.model.ClaimStatus;
import com.pla.grouplife.sharedresource.model.vo.Insured;
import com.pla.grouplife.sharedresource.model.vo.InsuredDependent;
import com.pla.sharedkernel.domain.model.ClaimType;
import com.pla.sharedkernel.domain.model.Policy;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Created by nthdimensioncompany on 4/11/2015.
 */
@Getter
@Setter
@NoArgsConstructor


public class GLClaimDetailDto {

   private String claimNumber;
    private String claimId;
    private List<ClaimType> claimTypes;
    private AssuredDetailDto assuredDetail;
    private Policy policy;
    private ClaimStatus claimStatus;
    //private ClaimRegistrationDto claimRegistrationDto;

    public GLClaimDetailDto withClaimNumberAndClaimId (String claimNumber, String claimId){
        this.claimNumber=claimNumber;
        this.claimId=claimId;
        return this;
    }
    public GLClaimDetailDto withPolicy(Policy policy){
        this.policy  = policy;
        return this;
    }

    public GLClaimDetailDto withInsuredAssuredDetail(Insured insured){
        this.assuredDetail = AssuredDetailDto.getInstance(insured);
        return this;
    }

    public GLClaimDetailDto withInsuredDependentAssuredDetail(InsuredDependent insuredDependent){
        this.assuredDetail = AssuredDetailDto.getInstance(insuredDependent);
        return this;
    }}

/*

    public GLClaimDetailDto withClaimRegistationDetail(ClaimRegistrationDto claimRegistrationDto){

    this.claimRegistrationDto=claimRegistrationDto;
        return this;
    }


 */
