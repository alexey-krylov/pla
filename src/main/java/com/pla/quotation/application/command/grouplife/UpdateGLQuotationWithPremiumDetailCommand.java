package com.pla.quotation.application.command.grouplife;

import com.pla.quotation.query.PremiumDetailDto;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Created by Samir on 4/14/2015.
 */
@Getter
@Setter
public class UpdateGLQuotationWithPremiumDetailCommand {

    private String quotationId;

    private PremiumDetailDto premiumDetailDto;

    private UserDetails userDetails;
}
