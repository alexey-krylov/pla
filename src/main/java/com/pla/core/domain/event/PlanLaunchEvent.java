package com.pla.core.domain.event;

import com.pla.sharedkernel.identifier.PlanId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * Created by Samir on 5/30/2015.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PlanLaunchEvent implements Serializable {

    private PlanId planId;

}
