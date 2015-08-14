package com.pla.grouphealth.policy.application.command;

import com.pla.grouphealth.policy.domain.model.GroupHealthPolicy;
import com.pla.grouphealth.policy.domain.service.GHPolicyFactory;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.axonframework.repository.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Samir on 7/9/2015.
 */
@Component
    public class GHPolicyCommandHandler {

        private GHPolicyFactory ghPolicyFactory;

        private Repository<GroupHealthPolicy> ghPolicyMongoRepository;


        @Autowired
        public GHPolicyCommandHandler(GHPolicyFactory ghPolicyFactory, Repository<GroupHealthPolicy> ghPolicyMongoRepository) {
            this.ghPolicyFactory = ghPolicyFactory;
            this.ghPolicyMongoRepository = ghPolicyMongoRepository;
        }

    @CommandHandler
    public void createPolicy(GHProposalToPolicyCommand proposalToPolicyCommand) {
        GroupHealthPolicy groupHealthPolicy = ghPolicyFactory.createPolicy(proposalToPolicyCommand.getProposalId());
        ghPolicyMongoRepository.add(groupHealthPolicy);
    }
}
