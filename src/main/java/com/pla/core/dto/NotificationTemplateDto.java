package com.pla.core.dto;

import com.pla.sharedkernel.domain.model.ProcessType;
import com.pla.sharedkernel.domain.model.WaitingForEnum;
import com.pla.sharedkernel.identifier.LineOfBusinessEnum;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by pradyumna on 18-06-2015.
 */
@Getter
@Setter
public class NotificationTemplateDto {
    private LineOfBusinessEnum lineOfBusiness;
    private ProcessType processType;
    private WaitingForEnum waitingFor;

}
