package com.pla.individuallife.sharedresource.model.vo;

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
    private String wine;
    private String beer;
    private String spirit;
    private int smokePerDay;
    private List<Question> questions;
}
