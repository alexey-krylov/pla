package com.pla.core.paypoint.application.service;

import com.pla.core.paypoint.application.command.PayPointCommand;
import com.pla.core.paypoint.domain.model.PayPoint;
import com.pla.core.paypoint.domain.model.PayPointId;
import org.nthdimenzion.common.service.JpaRepositoryFactory;
import org.nthdimenzion.ddd.domain.annotations.DomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Rudra on 12/11/2015.
 */
@DomainService
public class PayPointService {


    private JpaRepositoryFactory jpaRepositoryFactory;
    private JpaRepository<PayPoint, PayPointId> paypointRepository;

    @Autowired
    public PayPointService(JpaRepositoryFactory jpaRepositoryFactory){
        paypointRepository = jpaRepositoryFactory.getCrudRepository(PayPoint.class);
    }
    public PayPoint createPaypoint(PayPointCommand paypointCommand){
        PayPoint paypoint = paypointCommand.createAndSetPropertiesToPayPointEntity();
        return paypointRepository.save(paypoint);
    }

    public PayPointCommand getPayPointByPayPointId(PayPointId payPointId) {
        PayPoint payPoint = paypointRepository.findOne(payPointId);
        PayPointCommand payPointCommand = new PayPointCommand();
        return payPoint.setPropertiesToPayPointCommandFromPayPoint(payPointCommand);
    }
}
