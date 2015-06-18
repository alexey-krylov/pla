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
    private Question question_17;
    private Question question_18;
    private Question question_19;
    private Question question_20;
}
