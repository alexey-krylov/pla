package com.pla.individuallife.quotation.application.command.quotation;

import com.pla.core.domain.model.agent.AgentId;
import com.pla.sharedkernel.identifier.PlanId;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.security.core.userdetails.UserDetails;

import javax.validation.constraints.NotNull;

/**
 * Created by Karunakar on 5/13/2015.
 */
@Getter
@Setter
@NoArgsConstructor
public class CreateILQuotationCommand {

    @NotNull(message = "{Agent ID cannot be null}")
    private AgentId agentId;

    private String assuredId;

    @NotNull(message = "{Assured Title cannot be null}")
    @NotEmpty(message = "{Assured Title cannot be empty}")
    private String assuredTitle;

    @NotNull(message = "{Assured First Name  cannot be null}")
    @NotEmpty(message = "{Assured First Name  cannot be empty}")
    private String assuredFName;

    @NotNull(message = "{Assured Surname  cannot be null}")
    @NotEmpty(message = "{Assured Surname  cannot be empty}")
    private String assuredSurame;

    @NotNull(message = "{Assured NRC  cannot be null}")
    @NotEmpty(message = "{Assured NRC  cannot be empty}")
    private String assuredNRC;

    @NotNull(message = "{Plan ID cannot be null}")
    private PlanId planId;

    private UserDetails userDetails;
}
