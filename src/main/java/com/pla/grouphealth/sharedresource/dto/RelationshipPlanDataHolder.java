package com.pla.grouphealth.sharedresource.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Mohan Sharma on 11/20/2015.
 */
@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
public class RelationshipPlanDataHolder {
    private String relationship;
    private String planCode;
}
