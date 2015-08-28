package com.pla.individuallife.quotation.domain.event;

import com.pla.sharedkernel.identifier.QuotationId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Created by Admin on 8/27/2015.
 */
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ILQuotationSharedEvent {
    private QuotationId quotationId;
}
