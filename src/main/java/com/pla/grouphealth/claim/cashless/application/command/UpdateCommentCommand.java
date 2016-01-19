package com.pla.grouphealth.claim.cashless.application.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.DateTime;
import org.springframework.security.core.userdetails.UserDetails;

import javax.validation.constraints.NotNull;

/**
 * Created by Mohan Sharma on 1/18/2016.
 */
@NoArgsConstructor
@Getter
@Setter
public class UpdateCommentCommand {
    @NotEmpty(message = "preAuthorizationRequestId cannot be empty")
    @NotNull(message = "preAuthorizationRequestId cannot be empty")
    private String preAuthorizationRequestId;
    @NotEmpty(message = "comments cannot be empty")
    @NotNull(message = "comments cannot be empty")
    private String comments;
    private DateTime commentDateTime;
    private UserDetails userDetails;
}
