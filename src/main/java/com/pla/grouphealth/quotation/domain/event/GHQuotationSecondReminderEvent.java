package com.pla.grouphealth.quotation.domain.event;

import com.pla.sharedkernel.identifier.QuotationId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.nthdimenzion.axonframework.event.ISagaEvent;

/**
 * Created by Admin on 25-Nov-15.
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class GHQuotationSecondReminderEvent implements ISagaEvent {

    private QuotationId quotationId;

}
