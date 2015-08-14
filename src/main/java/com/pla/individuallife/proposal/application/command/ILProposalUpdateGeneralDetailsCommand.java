package com.pla.individuallife.proposal.application.command;

import com.pla.individuallife.sharedresource.model.vo.GeneralDetailQuestion;
import com.pla.individuallife.sharedresource.dto.QuestionAnswerDto;
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

    private GeneralDetailQuestion assuredByPLAL;
    private GeneralDetailQuestion assuredByOthers;
    private GeneralDetailQuestion pendingInsuranceByOthers;
    private GeneralDetailQuestion assuranceDeclined;
    private List<QuestionAnswerDto> generalQuestion;

    private UserDetails userDetails;
    private String proposalId;

}
