package com.pla.individuallife.proposal.domain.model;

import com.pla.individuallife.identifier.QuestionId;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by Prasant on 17-Jun-15.
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class QuestionAnswer {
    private QuestionId questionId;
    private boolean response;

    public QuestionAnswer(QuestionId questionId, boolean response) {
        this.questionId = questionId;
        this.response = response;
    }
}
