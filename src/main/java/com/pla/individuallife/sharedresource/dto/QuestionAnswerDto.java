package com.pla.individuallife.sharedresource.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Prasant on 04-Jun-15.
 */
@Getter
@Setter
@NoArgsConstructor
public class QuestionAnswerDto {

   private String questionId;
    private Boolean answer;

    public QuestionAnswerDto(String questionId, Boolean answer) {
        this.questionId = questionId;
        this.answer = answer;
    }
}
