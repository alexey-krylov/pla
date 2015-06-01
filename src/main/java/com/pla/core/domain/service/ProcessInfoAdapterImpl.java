package com.pla.core.domain.service;

import com.pla.core.domain.model.ProcessType;
import com.pla.publishedlanguage.contract.IProcessInfoAdapter;
import com.pla.sharedkernel.identifier.LineOfBusinessEnum;
import org.springframework.stereotype.Service;

/**
 * Created by Samir on 5/31/2015.
 */
@Service(value = "processInfoAdapter")
public class ProcessInfoAdapterImpl implements IProcessInfoAdapter {


    @Override
    public int getPurgeTimePeriod(LineOfBusinessEnum lineOfBusinessEnum, ProcessType processType) {
        return 0;
    }

    @Override
    public int getClosureTimePeriod(LineOfBusinessEnum lineOfBusinessEnum, ProcessType processType) {
        return 0;
    }

    @Override
    public int getDaysForFirstReminder(LineOfBusinessEnum lineOfBusinessEnum, ProcessType processType) {
        return 0;
    }

    @Override
    public int getDaysForSecondReminder(LineOfBusinessEnum lineOfBusinessEnum, ProcessType processType) {
        return 0;
    }
}
