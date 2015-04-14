package com.pla.quotation.presentation.command.grouplife;

import com.pla.sharedkernel.identifier.QuotationId;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Samir on 4/14/2015.
 */
@Getter
@Setter
public class GenerateGLQuotationCommand {

    private QuotationId quotationId;
}
