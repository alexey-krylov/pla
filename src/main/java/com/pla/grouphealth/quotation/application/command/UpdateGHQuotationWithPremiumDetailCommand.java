package com.pla.grouphealth.quotation.application.command;

import com.pla.grouphealth.quotation.query.GHPremiumDetailDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Created by Samir on 4/14/2015.
 */
@Getter
@Setter
@NoArgsConstructor
public class UpdateGHQuotationWithPremiumDetailCommand {

    private String quotationId;

    private GHPremiumDetailDto premiumDetailDto;

    private UserDetails userDetails;
}
