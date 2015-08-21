package com.pla.grouplife.sharedresource.event;

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
public class GLQuotationConvertedToProposalEvent {

    private String quotationNumber;

    private QuotationId quotationId;
}
