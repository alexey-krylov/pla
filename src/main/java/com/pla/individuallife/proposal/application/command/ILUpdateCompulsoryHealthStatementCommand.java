package com.pla.individuallife.proposal.application.command;

import com.pla.individuallife.proposal.domain.model.QuestionAnswer;
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

public class ILUpdateCompulsoryHealthStatementCommand  {
    private List<QuestionAnswer> questions;
    private UserDetails userDetails;
    private String proposalId;
}
