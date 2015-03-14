package com.pla.sharedkernel.identifier;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

/**
 * @author: pradyumna
 * @since 1.0 11/03/2015
 */
@Embeddable
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class PlanId {

    private String planId;
}
