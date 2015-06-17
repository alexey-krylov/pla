package com.pla.individuallife.proposal.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by ASUS on 17-Jun-15.
 */

@Getter
@Setter
@NoArgsConstructor
@ToString
public class HabitDetails {
    private int wine;
    private int beer;
    private int spirit;
    private int smokePerDay;
    Question question_17;
    Question question_18;
    Question question_19;
    Question question_20;
}
