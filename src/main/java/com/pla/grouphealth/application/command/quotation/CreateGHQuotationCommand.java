package com.pla.grouphealth.application.command.quotation;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.security.core.userdetails.UserDetails;

import javax.validation.constraints.NotNull;

/**
 * Created by Karunakar on 4/30/2015.
 */
@Getter
@Setter
@NoArgsConstructor
public class CreateGHQuotationCommand {

    @NotNull(message = "{Agent ID cannot be null}")
    @NotEmpty(message = "{Agent ID cannot be empty}")
    private String agentId;

    @NotNull(message = "{Proposer name cannot be null}")
    @NotEmpty(message = "{Proposer name cannot be empty}")
    private String proposerName;

    private String agentName;

    private String teamName;

    private String branchName;

    private UserDetails userDetails;
}
