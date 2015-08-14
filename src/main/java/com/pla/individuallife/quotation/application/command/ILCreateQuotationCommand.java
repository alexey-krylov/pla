package com.pla.individuallife.quotation.application.command;

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
public class ILCreateQuotationCommand {

    @NotNull(message = "{Agent ID cannot be null}")
    private String agentId;

    @NotNull(message = "{Assured Title cannot be null}")
    @NotEmpty(message = "{Assured Title cannot be empty}")
    private String title;

    @NotNull(message = "{Assured First Name  cannot be null}")
    @NotEmpty(message = "{Assured First Name  cannot be empty}")
    private String firstName;

    @NotNull(message = "{Assured Surname  cannot be null}")
    @NotEmpty(message = "{Assured Surname  cannot be empty}")
    private String surname;

    @NotNull(message = "{Assured NRC  cannot be null}")
    @NotEmpty(message = "{Assured NRC  cannot be empty}")
    private String nrc;

    @NotNull(message = "{Plan ID cannot be null}")
    private String planId;

    private UserDetails userDetails;

}
