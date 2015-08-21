package com.pla.grouphealth.quotation.application.command;

import com.pla.sharedkernel.identifier.QuotationId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Created by Samir on 6/30/2015.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GHSharedQuotationCommand {

    private QuotationId quotationId;
}
