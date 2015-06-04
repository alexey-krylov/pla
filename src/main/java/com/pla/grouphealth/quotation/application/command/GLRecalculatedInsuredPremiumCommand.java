package com.pla.grouphealth.quotation.application.command;

import com.pla.grouphealth.quotation.query.PremiumDetailDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Created by Samir on 6/3/2015.
 */
@Getter
@Setter
@NoArgsConstructor
public class GLRecalculatedInsuredPremiumCommand {

    private String quotationId;

    private PremiumDetailDto premiumDetailDto;

    private UserDetails userDetails;
}
