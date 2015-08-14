package com.pla.grouplife.policy.application.command;

import com.pla.grouplife.policy.domain.model.GroupLifePolicy;
import com.pla.grouplife.policy.domain.service.GLPolicyFactory;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.axonframework.repository.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Samir on 7/9/2015.
 */
@Component
public class GLPolicyCommandHandler {

    private GLPolicyFactory glPolicyFactory;

    private Repository<GroupLifePolicy> glPolicyMongoRepository;


    @Autowired
    public GLPolicyCommandHandler(GLPolicyFactory glPolicyFactory, Repository<GroupLifePolicy> glPolicyMongoRepository) {
        this.glPolicyFactory = glPolicyFactory;
        this.glPolicyMongoRepository = glPolicyMongoRepository;
    }

    @CommandHandler
    public void createPolicy(GLProposalToPolicyCommand proposalToPolicyCommand) {
        GroupLifePolicy groupLifePolicy = glPolicyFactory.createPolicy(proposalToPolicyCommand.getProposalId());
        glPolicyMongoRepository.add(groupLifePolicy);
    }
}
