package com.pla.individuallife.sharedresource.model.vo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Prasant on 12-Jun-15.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FamilyPersonalDetail {
    private FamilyHistoryDetail familyHistory;
    private HabitDetails habit;
    private BuildDetail build;
    private Boolean isPregnant;
    private int pregnancyMonth;
}
