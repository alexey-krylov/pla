package com.pla.individuallife.proposal.application.command;

import com.pla.individuallife.sharedresource.model.vo.ReplacementQuestion;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Created by Karunakar on 7/1/2015.
 */
@Getter
@Setter
@NoArgsConstructor
public class ILProposalUpdateAdditionalDetailsCommand {

    private String medicalAttendantDetails;
    private String medicalAttendantDuration;
    private String dateAndReason;
    private ReplacementQuestion replacementDetails;
    private UserDetails userDetails;
    private String proposalId;

}
