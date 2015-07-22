package com.pla.individuallife.quotation.application.command;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Created by User on 6/5/2015.
 */
@Getter
@Setter
public class ILGenerateQuotationCommand {

    private String quotationId;

    private UserDetails userDetails;
}
