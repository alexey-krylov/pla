package com.pla.core.application;

import lombok.*;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.security.core.userdetails.UserDetails;

import javax.validation.constraints.NotNull;

/**
 * Created by User on 3/30/2015.
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
@AllArgsConstructor
public class InactivateTeamCommand {

    @NotNull(message = "{team id cannot be null}")
    @NotEmpty(message = "{team id cannot be empty}")
    private String teamId;

    private UserDetails userDetails;
}
