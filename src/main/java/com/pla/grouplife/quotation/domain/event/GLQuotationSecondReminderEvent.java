package com.pla.grouplife.quotation.domain.event;

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
public class GLQuotationSecondReminderEvent implements ISagaEvent {

    private QuotationId quotationId;
}
