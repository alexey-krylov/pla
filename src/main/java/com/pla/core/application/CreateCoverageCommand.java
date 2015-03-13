/*
 * Copyright (c) 3/12/15 6:32 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.application;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * @author: Samir
 * @since 1.0 12/03/2015
 */
@Getter
@Setter
public class CreateCoverageCommand {

    private UserDetails userDetails;
}
