package com.pla.grouphealth.quotation.application.command;

import com.pla.sharedkernel.identifier.QuotationId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Created by Samir on 7/6/2015.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GHQuotationConvertedCommand {

    private QuotationId quotationId;
}
