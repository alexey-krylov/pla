package com.pla.core.dto;

import com.pla.sharedkernel.identifier.PlanId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Samir on 4/6/2015.
 */
@Getter
@Setter
@AllArgsConstructor
public class PlanDetailDto {

    private PlanId planId;

    private String planName;
}
