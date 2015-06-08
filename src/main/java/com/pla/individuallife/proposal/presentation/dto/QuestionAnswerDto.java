package com.pla.individuallife.proposal.presentation.dto;

import lombok.Getter;

/**
 * Created by Prasant on 04-Jun-15.
 */
@Getter
public class QuestionAnswerDto {

    Long questionid;
    Boolean response;

    public QuestionAnswerDto(long questionId, boolean response) {
        this.questionid = questionId;
        this.response = response;
    }
}
