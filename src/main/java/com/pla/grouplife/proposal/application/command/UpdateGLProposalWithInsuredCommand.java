package com.pla.grouplife.proposal.application.command;

import com.pla.grouplife.sharedresource.dto.InsuredDto;
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
public class UpdateGLProposalWithInsuredCommand {

    private String proposalId;

    private List<InsuredDto> insuredDtos;

    private UserDetails userDetails;

    private boolean samePlanForAllRelation;

    private boolean samePlanForAllCategory;
}
