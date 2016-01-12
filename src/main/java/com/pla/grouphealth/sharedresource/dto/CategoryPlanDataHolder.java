package com.pla.grouphealth.sharedresource.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Author - Mohan Sharma Created on 11/20/2015.
 */
@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
public class CategoryPlanDataHolder {
    private String category;
    private String planCode;
}
