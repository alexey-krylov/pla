package com.pla.individuallife.proposal.domain.model;

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
    private long questionId;
    private boolean response;

    public QuestionAnswer(long questionId, boolean response) {
        this.questionId = questionId;
        this.response = response;
    }
}
