package com.pla.quotation.application.command.grouplife;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Created by Samir on 4/14/2015.
 */
@Getter
@Setter
public class GenerateGLQuotationCommand {

    private String quotationId;

    private UserDetails userDetails;
}
