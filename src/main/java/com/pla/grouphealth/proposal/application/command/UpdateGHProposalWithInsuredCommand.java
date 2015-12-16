package com.pla.grouphealth.proposal.application.command;

import com.pla.grouphealth.sharedresource.dto.GHInsuredDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

/**
 * Created by Samir on 5/19/2015.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateGHProposalWithInsuredCommand {

    private String proposalId;

    private List<GHInsuredDto> insuredDtos;

    private UserDetails userDetails;

    private boolean samePlanForAllRelation;

    private boolean samePlanForAllCategory;

    private String schemeName;

}
