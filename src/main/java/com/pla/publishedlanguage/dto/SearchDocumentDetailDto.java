package com.pla.publishedlanguage.dto;

import com.pla.sharedkernel.identifier.CoverageId;
import com.pla.sharedkernel.identifier.PlanId;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by Admin on 7/7/2015.
 */
@Getter
@Setter
public class SearchDocumentDetailDto {

    private PlanId planId;

    private List<CoverageId> coverageIds;
}
