/*
 * Copyright (c) 3/13/15 8:04 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.domain.model.agent;

import com.google.common.base.Preconditions;
import lombok.*;
import org.hibernate.annotations.Immutable;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * @author: Samir
 * @since 1.0 13/03/2015
 */
@ValueObject
@Immutable
@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@EqualsAndHashCode(of = "agentId")
@Setter(value = AccessLevel.PACKAGE)
public class AgentId implements Serializable {

    private String agentId;

    public AgentId(String agentId) {
        Preconditions.checkArgument(agentId != null);
        this.agentId = agentId;
    }

    public String toString() {
        return agentId;
    }
}
