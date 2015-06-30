package com.pla.individuallife.proposal.domain.model;

import com.pla.individuallife.identifier.QuestionId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Prasant on 12-Jun-15.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Question {
    private QuestionId questionId;
    private boolean answer;
    private String answerResponse;

}
