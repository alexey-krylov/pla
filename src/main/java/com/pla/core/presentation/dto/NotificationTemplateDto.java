package com.pla.core.presentation.dto;

import com.pla.sharedkernel.domain.model.WaitingForEnum;
import com.pla.sharedkernel.domain.model.ProcessType;
import com.pla.sharedkernel.identifier.LineOfBusinessEnum;

/**
 * Created by pradyumna on 18-06-2015.
 */
public class NotificationTemplateDto {

    private LineOfBusinessEnum lineOfBusiness;
    private ProcessType processType;
    private WaitingForEnum waitingFor;

}
