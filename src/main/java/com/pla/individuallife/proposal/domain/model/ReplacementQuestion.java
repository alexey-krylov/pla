package com.pla.individuallife.proposal.domain.model;

import com.pla.individuallife.identifier.QuestionId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Embedded;

/**
 * Created by Karunakar on 7/9/2015.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReplacementQuestion {
    @Embedded
    private QuestionId questionId;
    private boolean answer;
    private String answerResponse1;
    private String answerResponse2;

}
