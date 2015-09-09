package com.pla.individuallife.policy.application;

import com.pla.individuallife.policy.domain.model.IndividualLifePolicy;
import com.pla.individuallife.policy.domain.service.ILPolicyFactory;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.axonframework.repository.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by Admin on 8/3/2015.
 */
@Component
public class ILPolicyCommandHandler {

    private ILPolicyFactory ilPolicyFactory;

    private Repository<IndividualLifePolicy> ilPolicyMongoRepository;


    @Autowired
    public ILPolicyCommandHandler(ILPolicyFactory ilPolicyFactory, Repository<IndividualLifePolicy> ilPolicyMongoRepository) {
        this.ilPolicyFactory = ilPolicyFactory;
        this.ilPolicyMongoRepository = ilPolicyMongoRepository;
    }

    @CommandHandler
    public void createPolicy(ILProposalToPolicyCommand proposalToPolicyCommand) throws InvocationTargetException, IllegalAccessException {
        IndividualLifePolicy individualLifePolicy = ilPolicyFactory.createPolicy(proposalToPolicyCommand.getProposalId());
        ilPolicyMongoRepository.add(individualLifePolicy);
    }
}
