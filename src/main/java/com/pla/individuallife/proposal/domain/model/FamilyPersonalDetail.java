package com.pla.individuallife.proposal.domain.model;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Prasant on 12-Jun-15.
 */
@Getter
@Setter
@NoArgsConstructor
public class FamilyPersonalDetail {
    private FamilyHistoryDetail familyHistory;
    private HabitDetails habit;
    private BuildDetail build;
    private Boolean isPregnant;
    private int pregnancyMonth;
}
