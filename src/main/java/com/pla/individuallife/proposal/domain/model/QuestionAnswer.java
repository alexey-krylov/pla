package com.pla.individuallife.proposal.domain.model;

import lombok.Getter;
import lombok.ToString;

/**
 * Created by Prasant on 17-Jun-15.
 */
@Getter
@ToString
public class QuestionAnswer {
    private Long questionId;
    private Boolean response;

    public QuestionAnswer(long questionId, boolean response) {
        this.questionId = questionId;
        this.response = response;
    }
}
