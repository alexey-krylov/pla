package com.pla.publishedlanguage.contract;

import com.pla.publishedlanguage.domain.model.PremiumFrequency;
import com.pla.sharedkernel.exception.ProcessInfoException;
import com.pla.sharedkernel.domain.model.ProcessType;
import com.pla.sharedkernel.identifier.LineOfBusinessEnum;

import java.math.BigDecimal;

/**
 * Created by Samir on 5/31/2015.
 */
public interface IProcessInfoAdapter {

    public int getPurgeTimePeriod(LineOfBusinessEnum lineOfBusinessEnum, ProcessType processType) throws ProcessInfoException;

    public int getClosureTimePeriod(LineOfBusinessEnum lineOfBusinessEnum, ProcessType processType) throws ProcessInfoException;

    public int getDaysForFirstReminder(LineOfBusinessEnum lineOfBusinessEnum, ProcessType processType) throws ProcessInfoException;

    public int getDaysForSecondReminder(LineOfBusinessEnum lineOfBusinessEnum, ProcessType processType) throws ProcessInfoException;

    public int getLapseTimePeriod(LineOfBusinessEnum lineOfBusinessEnum,PremiumFrequency premiumFrequency) throws ProcessInfoException;

    public int getDaysForFirstReminder(LineOfBusinessEnum lineOfBusinessEnum,PremiumFrequency premiumFrequency) throws ProcessInfoException;

    public int getDaysForSecondReminder(LineOfBusinessEnum lineOfBusinessEnum,PremiumFrequency premiumFrequency) throws ProcessInfoException;

    public BigDecimal getServiceTaxAmount();
}
