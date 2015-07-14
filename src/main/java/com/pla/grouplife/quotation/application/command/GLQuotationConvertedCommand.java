package com.pla.grouplife.quotation.application.command;

import com.pla.sharedkernel.identifier.QuotationId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Created by Samir on 7/14/2015.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GLQuotationConvertedCommand {

    private QuotationId quotationId;
}
