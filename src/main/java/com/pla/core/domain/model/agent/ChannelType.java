/*
 * Copyright (c) 3/16/15 4:59 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.domain.model.agent;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import javax.persistence.Embeddable;

import static com.google.common.base.Preconditions.checkArgument;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * @author: Samir
 * @since 1.0 16/03/2015
 */
@Embeddable
@ValueObject
@Immutable
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class ChannelType {

    private String channelCode;

    private String channelName;

    ChannelType(String channelCode, String channelName) {
        checkArgument(isNotEmpty(channelCode));
        checkArgument(isNotEmpty(channelName));
        this.channelCode = channelCode;
        this.channelName = channelName;
    }
}
