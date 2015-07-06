package com.pla.individuallife.proposal.domain.model;

import com.pla.individuallife.proposal.presentation.dto.QuestionAnswerDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * Created by Karunakar on 7/6/2015.
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class GeneralDetails {

    private List<GeneralDetailQuestions> generalQuestions;
    private List<QuestionAnswerDto> questionAndAnswers;
}
