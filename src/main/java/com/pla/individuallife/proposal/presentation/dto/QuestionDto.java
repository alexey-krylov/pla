package com.pla.individuallife.proposal.presentation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Karunakar on 6/30/2015.
 */
@Getter
@Setter
@NoArgsConstructor
public class QuestionDto {
    private String questionId;
    private boolean answer;
    private String answerResponse;

}
