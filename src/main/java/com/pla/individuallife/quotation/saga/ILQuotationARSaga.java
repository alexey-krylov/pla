package com.pla.individuallife.quotation.saga;

import com.pla.individuallife.quotation.domain.event.*;
import com.pla.individuallife.quotation.domain.model.ILQuotation;
import com.pla.individuallife.quotation.domain.model.ILQuotationAR;
import com.pla.publishedlanguage.contract.IProcessInfoAdapter;
import com.pla.sharedkernel.domain.model.ProcessType;
import com.pla.sharedkernel.exception.ProcessInfoException;
import com.pla.sharedkernel.identifier.LineOfBusinessEnum;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.axonframework.eventhandling.scheduling.EventScheduler;
import org.axonframework.repository.Repository;
import org.axonframework.saga.annotation.AbstractAnnotatedSaga;
import org.axonframework.saga.annotation.SagaEventHandler;
import org.axonframework.saga.annotation.StartSaga;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by pradyumna on 16-06-2015.
 */
@Component
public class ILQuotationARSaga extends AbstractAnnotatedSaga {

    private static final Logger LOGGER = LoggerFactory.getLogger(ILQuotationARSaga.class);
    @Autowired
    private transient IProcessInfoAdapter processInfoAdapter;
    @Autowired
    private transient EventScheduler eventScheduler;

    @Autowired
    private Repository<ILQuotationAR> ilQuotationARRepository;

    @Autowired
    private Repository<ILQuotation> ilQuotationRepository;

    //TODO discuss what happens if a quotation is just created and nothing happens after that
    //TODO Basically can it move from DRAFT to PURGE OR CLOSE OR ????

    @SagaEventHandler(associationProperty = "quotationId")
    public void handle(ILQuotationCreatedEvent event) throws ProcessInfoException {
        System.out.println(" Start Monitoring Quotations *** related to AR " + event.getQuotationARId());
    }

    @StartSaga
    @SagaEventHandler(associationProperty = "quotationId")
    public void handle(ILQuotationGeneratedEvent event) throws ProcessInfoException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Handling GL Quotation Generated Event .....", event);
        }
        int noOfDaysToPurge = processInfoAdapter.getPurgeTimePeriod(LineOfBusinessEnum.INDIVIDUAL_LIFE, ProcessType.QUOTATION);
        int noOfDaysToClosure = processInfoAdapter.getClosureTimePeriod(LineOfBusinessEnum.INDIVIDUAL_LIFE, ProcessType.QUOTATION);
        int firstReminderDay = processInfoAdapter.getDaysForFirstReminder(LineOfBusinessEnum.INDIVIDUAL_LIFE, ProcessType.QUOTATION);

        ILQuotation quotation = ilQuotationRepository.load(event.getQuotationId());
        LocalDate quotationGeneratedDate = quotation.getGeneratedOn();
        LocalDate firstReminderDate = quotationGeneratedDate.plusDays(firstReminderDay);
        LocalDate purgeDate = quotationGeneratedDate.plusDays(noOfDaysToPurge);
        LocalDate closureDate = quotationGeneratedDate.plusDays(noOfDaysToClosure);
        DateTime purgeScheduleDateTime = purgeDate.toDateTimeAtStartOfDay();
        DateTime closureScheduleDateTime = closureDate.toDateTimeAtStartOfDay();
        DateTime firstReminderDateTime = firstReminderDate.toDateTimeAtStartOfDay();

        eventScheduler.schedule(firstReminderDateTime, new ILQuotationReminderEvent(event.getQuotationId()));
        eventScheduler.schedule(purgeScheduleDateTime, new ILQuotationPurgeEvent(event.getQuotationId()));
        eventScheduler.schedule(closureScheduleDateTime, new ILQuotationClosureEvent(event.getQuotationId()));
    }


    @EventHandler()
    public void handle(ILQuotationVersionEvent event) throws ProcessInfoException {
        System.out.println(" Update Quotation Version Number " + event.getQuotationId());
        ILQuotationAR quotationAR = ilQuotationARRepository.load(event.getQuotationARId());
        ILQuotation quotation = ilQuotationRepository.load(event.getQuotationId());
        int version = quotationAR.incrementVersion();
        quotation.assignVersion(version);
    }
}
