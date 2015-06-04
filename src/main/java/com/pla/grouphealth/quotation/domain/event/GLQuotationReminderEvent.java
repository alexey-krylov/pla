package com.pla.grouphealth.quotation.domain.event;

import com.pla.sharedkernel.identifier.QuotationId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.nthdimenzion.axonframework.event.ISagaEvent;

/**
 * Created by Samir on 5/31/2015.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GLQuotationReminderEvent implements ISagaEvent {

    private QuotationId quotationId;
}
