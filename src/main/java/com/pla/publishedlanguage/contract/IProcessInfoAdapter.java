package com.pla.publishedlanguage.contract;

import com.pla.core.domain.model.ProcessType;
import com.pla.sharedkernel.identifier.LineOfBusinessEnum;

/**
 * Created by Samir on 5/31/2015.
 */
public interface IProcessInfoAdapter {

    public int getPurgeTimePeriod(LineOfBusinessEnum lineOfBusinessEnum, ProcessType processType);

    public int getClosureTimePeriod(LineOfBusinessEnum lineOfBusinessEnum, ProcessType processType);

    public int getDaysForFirstReminder(LineOfBusinessEnum lineOfBusinessEnum, ProcessType processType);

    public int getDaysForSecondReminder(LineOfBusinessEnum lineOfBusinessEnum, ProcessType processType);
}
