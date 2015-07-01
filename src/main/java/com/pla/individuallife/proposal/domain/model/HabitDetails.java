package com.pla.individuallife.proposal.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

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
    private List<Question> questions;
}
