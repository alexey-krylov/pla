package com.pla.grouplife.endorsement.application.command;

import com.pla.grouplife.endorsement.domain.model.GroupLifeEndorsement;
import com.pla.grouplife.endorsement.domain.service.GroupLifeEndorsementService;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.axonframework.repository.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Samir on 8/27/2015.
 */
@Component
public class GLEndorsementCommandHandler {


    private Repository<GroupLifeEndorsement> glEndorsementMongoRepository;

    private GroupLifeEndorsementService groupLifeEndorsementService;

    @Autowired
    public GLEndorsementCommandHandler(Repository<GroupLifeEndorsement> glEndorsementMongoRepository, GroupLifeEndorsementService groupLifeEndorsementService) {
        this.glEndorsementMongoRepository = glEndorsementMongoRepository;
        this.groupLifeEndorsementService = groupLifeEndorsementService;
    }

    @CommandHandler
    public String handle(GLCreateEndorsementCommand glCreateEndorsementCommand) {
        GroupLifeEndorsement groupLifeEndorsement = groupLifeEndorsementService.createEndorsement(glCreateEndorsementCommand.getPolicyId(), glCreateEndorsementCommand.getEndorsementType(), glCreateEndorsementCommand.getUserDetails());
        glEndorsementMongoRepository.add(groupLifeEndorsement);
        return groupLifeEndorsement.getEndorsementId().getEndorsementId();
    }
}
