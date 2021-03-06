package com.pla.individuallife.proposal.application.command;

import com.pla.individuallife.proposal.presentation.dto.QuestionDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

/**
 * Created by Prasant on 03-Jun-15.
 */
@Getter
@Setter
@NoArgsConstructor
public class ILProposalUpdateCompulsoryHealthStatementCommand {

    private List<QuestionDto> compulsoryHealthDetails;
    private UserDetails userDetails;
    private String proposalId;
}
