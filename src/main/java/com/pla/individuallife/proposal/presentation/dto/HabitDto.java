package com.pla.individuallife.proposal.presentation.dto;

import com.pla.individuallife.proposal.domain.model.Question;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Prasant on 12-Jun-15.
 */
@Getter
@Setter
@NoArgsConstructor
public class HabitDto {
    private int wine;
    private int beer;
    private int spirit;
    private int smokePerDay;
    Question question_17;
    Question question_18;
    Question question_19;
    Question question_20;
}
