package com.pla.sharedkernel.application;

import com.pla.sharedkernel.domain.model.ProcessType;
import com.pla.sharedkernel.domain.model.ReminderTypeEnum;
import com.pla.sharedkernel.domain.model.WaitingForEnum;
import com.pla.sharedkernel.identifier.LineOfBusinessEnum;
import com.pla.sharedkernel.identifier.QuotationId;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Admin on 6/30/2015.
 */
@Getter
@Setter
@NoArgsConstructor
public class CreateQuotationNotificationCommand extends CreateNotificationCommand{

    private QuotationId quotationId;

    public CreateQuotationNotificationCommand(QuotationId quotationId,String roleType, LineOfBusinessEnum lineOfBusiness, ProcessType processType,
                                              WaitingForEnum waitingFor, ReminderTypeEnum reminderType) {
        super(roleType, lineOfBusiness, processType, waitingFor, reminderType);
        this.quotationId = quotationId;
    }
}
