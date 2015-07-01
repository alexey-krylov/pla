package com.pla.individuallife.proposal.application.command;

import com.pla.individuallife.proposal.presentation.dto.QuestionDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

/**
 * Created by Karunakar on 7/1/2015.
 */
@Getter
@Setter
@NoArgsConstructor
public class ILProposalUpdateGeneralDetailsCommand {

    private List<QuestionDto> generateDetails;
    private UserDetails userDetails;
    private String proposalId;

}
