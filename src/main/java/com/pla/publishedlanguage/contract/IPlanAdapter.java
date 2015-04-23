package com.pla.publishedlanguage.contract;

import com.pla.publishedlanguage.dto.PlanCoverageDetailDto;
import com.pla.sharedkernel.identifier.PlanId;

import java.util.List;

/**
 * Created by Samir on 4/22/2015.
 */
public interface IPlanAdapter {

    List<PlanCoverageDetailDto> getPlanAndCoverageDetail(List<PlanId> planIds);

}
