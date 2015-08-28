package com.pla.individuallife.quotation.application.command;

import com.pla.sharedkernel.identifier.QuotationId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Created by Admin on 8/27/2015.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ShareILQuotationCommand {
    private QuotationId quotationId;
}
