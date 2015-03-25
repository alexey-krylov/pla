package com.pla.core.application;

import lombok.*;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

/**
 * Created with IntelliJ IDEA.
 * User: Admin
 * Date: 3/23/15
 * Time: 11:22 AM
 * To change this template use File | Settings | File Templates.
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
@AllArgsConstructor
public class MarkCoverageAsUsedCommand {

    @NotNull(message = "{coverage id cannot be null}")
    @NotEmpty(message = "{coverage id cannot be empty}")
    private String coverageId;
}