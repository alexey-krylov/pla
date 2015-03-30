package com.pla.core.application;

import com.pla.core.domain.model.ProcessType;
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
public class CreateMandatoryDocumentCommand {

    private UserDetails userDetails;

    @NotNull(message = "{plan id cannot be null}")
    @NotEmpty(message = "{plan id cannot be empty}")
    private String planId;

    private String coverageId;

    private ProcessType process;

    private Set<String> documents;

}

