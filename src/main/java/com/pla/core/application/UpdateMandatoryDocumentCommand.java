package com.pla.core.application;

import lombok.*;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.security.core.userdetails.UserDetails;

import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * Created by Admin on 3/27/2015.
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
@AllArgsConstructor
public class UpdateMandatoryDocumentCommand {

    private UserDetails userDetails;

    @NotNull(message = "{id cannot be null}")
    @NotEmpty(message = "{id cannot be empty}")
    private String id;

    private Set<String> documents;
}
