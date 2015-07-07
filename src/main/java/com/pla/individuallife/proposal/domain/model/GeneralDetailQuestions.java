package com.pla.individuallife.proposal.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

/**
 * Created by Karunakar on 7/6/2015.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GeneralDetailQuestions {
    private String questionId;
    private boolean answer;
    private Set<GeneralDetailsQuestionResponse> answerResponse;

}
