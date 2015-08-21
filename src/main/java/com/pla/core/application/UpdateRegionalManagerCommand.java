/*
 * Copyright (c) 3/10/15 1:39 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.application;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.joda.time.LocalDate;
import org.nthdimenzion.presentation.LocalJodaDateDeserializer;

/**
 * @author: Samir
 * @since 1.0 10/03/2015
 */

@NoArgsConstructor
@Getter
@Setter
@ToString
public class UpdateRegionalManagerCommand {

    private String regionCode;

    private String employeeId;

    private String firstName;

    private String lastName;

    @JsonDeserialize(using = LocalJodaDateDeserializer.class)
    private LocalDate fromDate;

}
