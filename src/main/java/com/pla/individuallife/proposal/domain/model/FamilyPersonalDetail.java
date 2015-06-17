package com.pla.individuallife.proposal.domain.model;

import com.pla.individuallife.proposal.presentation.dto.BuildDto;
import com.pla.individuallife.proposal.presentation.dto.FamilyHistoryDto;
import com.pla.individuallife.proposal.presentation.dto.HabitDto;
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
    private FamilyHistoryDto familyHistory;
    private HabitDto habit;
    private BuildDto build;
    private boolean isPregnant;
    private int pregnancyMonth;
}
