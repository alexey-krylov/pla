package com.pla.grouphealth.application.command.quotation;

import com.pla.grouphealth.query.PremiumDetailDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Created by Karunakar on 4/30/2015.
 */
@Getter
@Setter
@NoArgsConstructor
public class UpdateGHQuotationWithPremiumDetailCommand {

    private String quotationId;

    private PremiumDetailDto premiumDetailDto;

    private UserDetails userDetails;
}
