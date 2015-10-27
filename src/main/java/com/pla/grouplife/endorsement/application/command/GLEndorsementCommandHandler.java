package com.pla.grouplife.endorsement.application.command;

import com.google.common.collect.Sets;
import com.pla.grouplife.endorsement.domain.model.GLEndorsement;
import com.pla.grouplife.endorsement.domain.model.GLMemberDeletionEndorsement;
import com.pla.grouplife.endorsement.domain.model.GLMemberEndorsement;
import com.pla.grouplife.endorsement.domain.model.GroupLifeEndorsement;
import com.pla.grouplife.endorsement.domain.service.GroupLifeEndorsementService;
import com.pla.grouplife.endorsement.dto.GLEndorsementInsuredDto;
import com.pla.grouplife.sharedresource.dto.InsuredDto;
import com.pla.grouplife.sharedresource.model.GLEndorsementType;
import com.pla.grouplife.sharedresource.model.vo.*;
import com.pla.sharedkernel.domain.model.FamilyId;
import com.pla.sharedkernel.domain.model.Relationship;
import com.pla.sharedkernel.identifier.EndorsementId;
import com.pla.sharedkernel.identifier.PlanId;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.axonframework.repository.Repository;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.nthdimenzion.utils.UtilValidator.isEmpty;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Samir on 8/27/2015.
 */
@Component
public class GLEndorsementCommandHandler {


    private Repository<GroupLifeEndorsement> glEndorsementMongoRepository;

    private GroupLifeEndorsementService groupLifeEndorsementService;

    @Autowired
    private GridFsTemplate gridFsTemplate;

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

    @CommandHandler
    public String handle(GLEndorsementCommand glEndorsementCommand) {
        GroupLifeEndorsement groupLifeEndorsement = glEndorsementMongoRepository.load(glEndorsementCommand.getEndorsementId());
        GLEndorsement glEndorsement = groupLifeEndorsement.getEndorsement() != null ? groupLifeEndorsement.getEndorsement() : new GLEndorsement();
        glEndorsement = populateGLEndorsement(glEndorsement, glEndorsementCommand.getGlEndorsementInsuredDto(), glEndorsementCommand.getGlEndorsementType());
        groupLifeEndorsement.updateWithEndorsementDetail(glEndorsement);
        return groupLifeEndorsement.getEndorsementId().getEndorsementId();
    }

    private GLEndorsement populateGLEndorsement(GLEndorsement glEndorsement, GLEndorsementInsuredDto glEndorsementInsuredDto, GLEndorsementType glEndorsementType) {
        if (GLEndorsementType.ASSURED_MEMBER_ADDITION.equals(glEndorsementType)) {
            GLMemberEndorsement glMemberEndorsement = new GLMemberEndorsement(createInsuredDetail(glEndorsementInsuredDto.getInsureds()));
            glEndorsement.addMemberEndorsement(glMemberEndorsement);
        }
        if (GLEndorsementType.ASSURED_MEMBER_DELETION.equals(glEndorsementType)) {
            glEndorsement.addMemberDeletionEndorsement(createGLMemberDeletionEndorsement(glEndorsementInsuredDto));
        }
        return glEndorsement;
    }

    private List<GLMemberDeletionEndorsement> createGLMemberDeletionEndorsement(GLEndorsementInsuredDto glEndorsementInsuredDto) {
        List<GLMemberDeletionEndorsement> glMemberDeletionEndorsements = glEndorsementInsuredDto.getInsureds().stream().map(new Function<InsuredDto, GLMemberDeletionEndorsement>() {
            @Override
            public GLMemberDeletionEndorsement apply(InsuredDto insuredDto) {
                GLMemberDeletionEndorsement glMemberDeletionEndorsement = new GLMemberDeletionEndorsement(insuredDto.getCategory(),
                        isNotEmpty(insuredDto.getRelationship()) ? Relationship.getRelationship(insuredDto.getRelationship()) : null,
                        insuredDto.getNoOfAssured(), insuredDto.getFamilyId() != null ? new FamilyId(insuredDto.getFamilyId()) : null);
                return glMemberDeletionEndorsement;
            }
        }).collect(Collectors.toList());
        return glMemberDeletionEndorsements;
    }


    public Set<Insured> createInsuredDetail(List<InsuredDto> insuredDtos) {
        Set<Insured> insureds = insuredDtos.stream().map(new Function<InsuredDto, Insured>() {
            @Override
            public Insured apply(InsuredDto insuredDto) {
                final InsuredBuilder[] insuredBuilder = {Insured.getInsuredBuilder(new PlanId(insuredDto.getPlanPremiumDetail().getPlanId()), insuredDto.getPlanPremiumDetail().getPlanCode(), insuredDto.getPlanPremiumDetail().getPremiumAmount(), insuredDto.getPlanPremiumDetail().getSumAssured())};
                insuredBuilder[0].withCategory(insuredDto.getOccupationCategory()).withInsuredName(insuredDto.getSalutation(), insuredDto.getFirstName(), insuredDto.getLastName())
                        .withAnnualIncome(insuredDto.getAnnualIncome()).withOccupation(insuredDto.getOccupationClass()).
                        withInsuredNrcNumber(insuredDto.getNrcNumber()).withCompanyName(insuredDto.getCompanyName()).withFamilyId(insuredDto.getFamilyId())
                        .withManNumber(insuredDto.getManNumber()).withDateOfBirth(insuredDto.getDateOfBirth()).withGender(insuredDto.getGender()).withNoOfAssured(insuredDto.getNoOfAssured());
                Set<InsuredDependent> insuredDependents = getInsuredDependent(insuredDto.getInsuredDependents());
                insuredBuilder[0] = insuredBuilder[0].withDependents(insuredDependents);
                return insuredBuilder[0].build();
            }
        }).collect(Collectors.toSet());
        return insureds;
    }


    private Set<InsuredDependent> getInsuredDependent(Set<InsuredDto.InsuredDependentDto> insuredDependentDtos) {
        Set<InsuredDependent> insuredDependents = insuredDependentDtos.stream().map(new Function<InsuredDto.InsuredDependentDto, InsuredDependent>() {
            @Override
            public InsuredDependent apply(InsuredDto.InsuredDependentDto insuredDependentDto) {
                InsuredDto.PlanPremiumDetailDto premiumDetail = insuredDependentDto.getPlanPremiumDetail();

                final InsuredDependentBuilder[] insuredDependentBuilder = {InsuredDependent.getInsuredDependentBuilder(new PlanId(premiumDetail.getPlanId()), premiumDetail.getPlanCode(), null, premiumDetail.getSumAssured())};
                insuredDependentBuilder[0].withCategory(insuredDependentDto.getOccupationCategory()).withInsuredName(insuredDependentDto.getSalutation(), insuredDependentDto.getFirstName(), insuredDependentDto.getLastName())
                        .withInsuredNrcNumber(insuredDependentDto.getNrcNumber()).withCompanyName(insuredDependentDto.getCompanyName()).withOccupationClass(insuredDependentDto.getOccupationClass())
                        .withDateOfBirth(insuredDependentDto.getDateOfBirth()).withGender(insuredDependentDto.getGender())
                        .withRelationship(insuredDependentDto.getRelationship()).withNoOfAssured(insuredDependentDto.getNoOfAssured()).withFamilyId(insuredDependentDto.getFamilyId());
                return insuredDependentBuilder[0].build();
            }
        }).collect(Collectors.toSet());
        return insuredDependents;
    }


    @CommandHandler
    public void uploadMandatoryDocument(GLEndorsementDocumentCommand glEndorsementDocumentCommand) throws IOException {
        GroupLifeEndorsement groupLifeEndorsement = glEndorsementMongoRepository.load(new EndorsementId(glEndorsementDocumentCommand.getEndorsementId()));
        String fileName = glEndorsementDocumentCommand.getFile() != null ? glEndorsementDocumentCommand.getFile().getOriginalFilename() : "";
        Set<GLProposerDocument> documents = groupLifeEndorsement.getProposerDocuments();
        if (isEmpty(documents)) {
            documents = Sets.newHashSet();
        }
        String gridFsDocId = gridFsTemplate.store(glEndorsementDocumentCommand.getFile().getInputStream(), fileName, glEndorsementDocumentCommand.getFile().getContentType()).getId().toString();
        GLProposerDocument currentDocument = new GLProposerDocument(glEndorsementDocumentCommand.getDocumentId(), fileName, gridFsDocId, glEndorsementDocumentCommand.getFile().getContentType(), glEndorsementDocumentCommand.isMandatory());
        if (!documents.add(currentDocument)) {
            GLProposerDocument existingDocument = documents.stream().filter(new Predicate<GLProposerDocument>() {
                @Override
                public boolean test(GLProposerDocument ghProposerDocument) {
                    return currentDocument.equals(ghProposerDocument);
                }
            }).findAny().get();
            gridFsTemplate.delete(new Query(Criteria.where("_id").is(existingDocument.getGridFsDocId())));
            existingDocument = existingDocument.updateWithNameAndContent(glEndorsementDocumentCommand.getFilename(), gridFsDocId, glEndorsementDocumentCommand.getFile().getContentType());
        }
        groupLifeEndorsement = groupLifeEndorsement.updateWithDocuments(documents);
    }


    @CommandHandler
    public String submitProposal(SubmitGLEndorsementCommand submitGLEndorsementCommand) {
        GroupLifeEndorsement groupLifeEndorsement = glEndorsementMongoRepository.load(new EndorsementId(submitGLEndorsementCommand.getEndorsementId()));
        groupLifeEndorsement = groupLifeEndorsement.submitForApproval(DateTime.now(), submitGLEndorsementCommand.getUserDetails().getUsername(), submitGLEndorsementCommand.getComment());
        return groupLifeEndorsement.getIdentifier().getEndorsementId();
    }

    @CommandHandler
    public String returnEndorsement(ReturnGLEndorsementCommand returnGLEndorsementCommand) {
        GroupLifeEndorsement groupLifeEndorsement = glEndorsementMongoRepository.load(new EndorsementId(returnGLEndorsementCommand.getEndorsementId()));
        groupLifeEndorsement = groupLifeEndorsement.returnEndorsement(returnGLEndorsementCommand.getStatus(),returnGLEndorsementCommand.getUserDetails().getUsername(), returnGLEndorsementCommand.getComment());
        return groupLifeEndorsement.getIdentifier().getEndorsementId();
    }

    /*
    *
    * @TODO need to generate the Endorsement number once the endorsement request got approved by the approver
    * */
    @CommandHandler
    public String approve(ApproveGLEndorsementCommand approveGLEndorsementCommand) {
        GroupLifeEndorsement groupLifeEndorsement = glEndorsementMongoRepository.load(new EndorsementId(approveGLEndorsementCommand.getEndorsementId()));
        groupLifeEndorsement = groupLifeEndorsement.approve(DateTime.now(), approveGLEndorsementCommand.getUserDetails().getUsername(), approveGLEndorsementCommand.getComment());
        return groupLifeEndorsement.getIdentifier().getEndorsementId();
    }

}
