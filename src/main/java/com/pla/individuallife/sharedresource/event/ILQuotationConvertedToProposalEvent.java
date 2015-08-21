package com.pla.individuallife.sharedresource.event;

import com.pla.sharedkernel.identifier.QuotationId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Created by Karunakar on 7/6/2015.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ILQuotationConvertedToProposalEvent {

    private String quotationNumber;

    private QuotationId quotationId;
}