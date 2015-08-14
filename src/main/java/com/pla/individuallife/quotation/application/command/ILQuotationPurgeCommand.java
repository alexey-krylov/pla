package com.pla.individuallife.quotation.application.command;

import com.pla.sharedkernel.identifier.QuotationId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Created by Admin on 7/28/2015.
 */

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ILQuotationPurgeCommand {

    private QuotationId quotationId;
}
