package com.pla.grouplife.endorsement.application.command;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.pla.grouphealth.policy.domain.service.GLEndorsementUniqueNumberGenerator;
import com.pla.grouplife.endorsement.application.service.GLEndorsementService;
import com.pla.grouplife.endorsement.domain.model.*;
import com.pla.grouplife.endorsement.domain.service.GroupLifeEndorsementRoleAdapter;
import com.pla.grouplife.endorsement.domain.service.GroupLifeEndorsementService;
import com.pla.grouplife.endorsement.dto.GLEndorsementInsuredDto;
import com.pla.grouplife.proposal.presentation.dto.GLProposalMandatoryDocumentDto;
import com.pla.grouplife.sharedresource.dto.InsuredDto;
import com.pla.grouplife.sharedresource.dto.PremiumDetailDto;
import com.pla.grouplife.sharedresource.model.GLEndorsementType;
import com.pla.grouplife.sharedresource.model.vo.*;
import com.pla.grouplife.sharedresource.query.GLFinder;
import com.pla.sharedkernel.domain.model.EndorsementStatus;
import com.pla.sharedkernel.domain.model.EndorsementUniqueNumber;
import com.pla.sharedkernel.domain.model.FamilyId;
import com.pla.sharedkernel.domain.model.Relationship;
import com.pla.sharedkernel.identifier.EndorsementId;
import com.pla.sharedkernel.identifier.PlanId;
import com.pla.sharedkernel.util.SequenceGenerator;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.axonframework.repository.Repository;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.pla.grouplife.endorsement.exception.GLEndorsementException.raiseMandatoryDocumentNotUploaded;
import static org.nthdimenzion.utils.UtilValidator.isEmpty;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Samir on 8/27/2015.
 */
@Component
public class GLEndorsementCommandHandler {


    private Repository<GroupLifeEndorsement> glEndorsementMongoRepository;

    private GroupLifeEndorsementService groupLifeEndorsementService;

    private GLEndorsementUniqueNumberGenerator glEndorsementUniqueNumberGenerator;

    private GroupLifeEndorsementRoleAdapter glEndorsementRoleAdapter;

    private GLEndorsementService glEndorsementService;

    @Autowired
    private GLFinder glFinder;

    @Autowired
    private GridFsTemplate gridFsTemplate;
    @Autowired
    private SequenceGenerator sequenceGenerator;

    @Autowired
    public GLEndorsementCommandHandler(Repository<GroupLifeEndorsement> glEndorsementMongoRepository, GroupLifeEndorsementService groupLifeEndorsementService, GLEndorsementUniqueNumberGenerator glEndorsementUniqueNumberGenerator, GroupLifeEndorsementRoleAdapter glEndorsementRoleAdapter, GLEndorsementService glEndorsementService) {
        this.glEndorsementMongoRepository = glEndorsementMongoRepository;
        this.groupLifeEndorsementService = groupLifeEndorsementService;
        this.glEndorsementUniqueNumberGenerator = glEndorsementUniqueNumberGenerator;
        this.glEndorsementRoleAdapter = glEndorsementRoleAdapter;
        this.glEndorsementService = glEndorsementService;
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


    /*
    * @TODO change according to type
    * */
    private GLEndorsement populateGLEndorsement(GLEndorsement glEndorsement, GLEndorsementInsuredDto glEndorsementInsuredDto, GLEndorsementType glEndorsementType) {
        if (GLEndorsementType.ASSURED_MEMBER_ADDITION.equals(glEndorsementType)) {
            GLMemberEndorsement glMemberEndorsement = new GLMemberEndorsement(populateFamilyId(createInsuredDetail(glEndorsementInsuredDto.getInsureds())));
            glEndorsement.addMemberEndorsement(glMemberEndorsement);
        }
        if (GLEndorsementType.MEMBER_PROMOTION.equals(glEndorsementType)) {
            GLMemberEndorsement glMemberEndorsement = new GLMemberEndorsement(populateFamilyId(createInsuredDetail(glEndorsementInsuredDto.getInsureds())));
            glEndorsement.addPremiumEndorsement(glMemberEndorsement);
        }
        if (GLEndorsementType.ASSURED_MEMBER_DELETION.equals(glEndorsementType)) {
            GLMemberEndorsement glMemberEndorsement = new GLMemberEndorsement(createInsuredDetail(glEndorsementInsuredDto.getInsureds()));
            glEndorsement.addMemberDeletionEndorsement(glMemberEndorsement);
        }
        if (GLEndorsementType.NEW_CATEGORY_RELATION.equals(glEndorsementType)) {
            GLMemberEndorsement glMemberEndorsement = new GLMemberEndorsement(populateFamilyId(createInsuredDetail(glEndorsementInsuredDto.getInsureds())));
            glEndorsement.addNewCategoryRelationEndorsement(glMemberEndorsement);
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
                final InsuredBuilder[] insuredBuilder = {insuredDto.getPlanPremiumDetail()!=null?Insured.getInsuredBuilder(new PlanId(insuredDto.getPlanPremiumDetail().getPlanId()), insuredDto.getPlanPremiumDetail().getPlanCode(), insuredDto.getPlanPremiumDetail().getPremiumAmount(), insuredDto.getPlanPremiumDetail().getSumAssured(), insuredDto.getPlanPremiumDetail().getIncomeMultiplier() ):
                        new InsuredBuilder()};
                insuredBuilder[0].withCategory(insuredDto.getCategory()).withInsuredName(insuredDto.getSalutation(), insuredDto.getFirstName(), insuredDto.getLastName())
                        .withAnnualIncome(insuredDto.getAnnualIncome()).withOccupation(insuredDto.getOccupationClass()).withOlAnnualIncome(insuredDto.getOldAnnualIncome()).
                        withInsuredNrcNumber(insuredDto.getNrcNumber()).withCompanyName(insuredDto.getCompanyName()).withFamilyId(insuredDto.getFamilyId()).withManNumber(insuredDto.getManNumber())
                        .withManNumber(insuredDto.getManNumber()).withDateOfBirth(insuredDto.getDateOfBirth()).withGender(insuredDto.getGender()).withNoOfAssured(insuredDto.getNoOfAssured());
                Set<InsuredDependent> insuredDependents = getInsuredDependent(insuredDto.getInsuredDependents());
                insuredBuilder[0] = insuredBuilder[0].withDependents(insuredDependents);
                return insuredBuilder[0].build();
            }
        }).collect(Collectors.toSet());
        return insureds;
    }


    private Set<InsuredDependent> getInsuredDependent(Set<InsuredDto.InsuredDependentDto> insuredDependentDtos) {
        if (isEmpty(insuredDependentDtos)){
            return Collections.EMPTY_SET;
        }
        Set<InsuredDependent> insuredDependents = insuredDependentDtos.stream().map(new Function<InsuredDto.InsuredDependentDto, InsuredDependent>() {
            @Override
            public InsuredDependent apply(InsuredDto.InsuredDependentDto insuredDependentDto) {
                InsuredDto.PlanPremiumDetailDto premiumDetail = insuredDependentDto.getPlanPremiumDetail();

                final InsuredDependentBuilder[] insuredDependentBuilder = {InsuredDependent.getInsuredDependentBuilder(new PlanId(premiumDetail.getPlanId()), premiumDetail.getPlanCode(), premiumDetail.getPremiumAmount(), premiumDetail.getSumAssured())};
                insuredDependentBuilder[0].withCategory(insuredDependentDto.getCategory()).withInsuredName(insuredDependentDto.getSalutation(), insuredDependentDto.getFirstName(), insuredDependentDto.getLastName())
                        .withInsuredNrcNumber(insuredDependentDto.getNrcNumber()).withCompanyName(insuredDependentDto.getCompanyName()).withOccupationClass(insuredDependentDto.getOccupationClass())
                        .withDateOfBirth(insuredDependentDto.getDateOfBirth()).withGender(insuredDependentDto.getGender()).withManNumber(insuredDependentDto.getManNumber()).withFamilyId(insuredDependentDto.getFamilyId())
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
        currentDocument.setApproved(true);
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

    @CommandHandler
    public String waiveDocumentCommandHandler(GLWaiveMandatoryDocumentCommand cmd) {
        GLEndorsementApprover glEndorsementApprover =  glEndorsementRoleAdapter.userToEndorsmentApprover(cmd.getUserDetails());
        GroupLifeEndorsement groupLifeEndorsement = glEndorsementMongoRepository.load(new EndorsementId(cmd.getEndorsementId()));
        Set<GLProposerDocument> documents = groupLifeEndorsement.getProposerDocuments();
        if (isEmpty(documents)) {
            documents = Sets.newHashSet();
        }
        for (GLProposalMandatoryDocumentDto proposalDocument : cmd.getWaivedDocuments()){
            GLProposerDocument glProposerDocument = new GLProposerDocument(proposalDocument.getDocumentId(),proposalDocument.getMandatory(),proposalDocument.getIsApproved());
            documents.add(glProposerDocument);
        }
        groupLifeEndorsement = glEndorsementApprover.updateWithDocuments(groupLifeEndorsement,documents);
        return groupLifeEndorsement.getIdentifier().getEndorsementId();
    }


    @CommandHandler
    public String approve(ApproveGLEndorsementCommand approveGLEndorsementCommand) throws ParseException {
        GroupLifeEndorsement groupLifeEndorsement = glEndorsementMongoRepository.load(new EndorsementId(approveGLEndorsementCommand.getEndorsementId()));
        if (EndorsementStatus.APPROVED.equals(approveGLEndorsementCommand.getStatus()) && !glEndorsementService.doesAllDocumentWaivesByApprover(approveGLEndorsementCommand.getEndorsementId())){
            raiseMandatoryDocumentNotUploaded();
        }
        String endorsementNo = glEndorsementUniqueNumberGenerator.getEndorsementUniqueNumber(GroupLifeEndorsement.class);
        EndorsementUniqueNumber endorseNumber = new EndorsementUniqueNumber(endorsementNo);
        Map policyMap = glFinder.findPolicyById(groupLifeEndorsement.getPolicy().getPolicyId().getPolicyId());
        PremiumDetail premiumDetail = (PremiumDetail) policyMap.get("premiumDetail");
        Industry industry = (Industry) policyMap.get("industry");
        PremiumDetailDto premiumDetailDto = new PremiumDetailDto(premiumDetail.getAddOnBenefit(),premiumDetail.getProfitAndSolvency(),premiumDetail.getHivDiscount(),
                premiumDetail.getValuedClientDiscount(),premiumDetail.getLongTermDiscount(),premiumDetail.getPolicyTermValue());
        groupLifeEndorsement = groupLifeEndorsementService.populateAnnualBasicPremiumOfInsured(groupLifeEndorsement, approveGLEndorsementCommand.getUserDetails(), premiumDetailDto, industry);
        groupLifeEndorsement = groupLifeEndorsement.approve(DateTime.now(), approveGLEndorsementCommand.getUserDetails().getUsername(), approveGLEndorsementCommand.getComment(), endorseNumber);
        return groupLifeEndorsement.getIdentifier().getEndorsementId();
    }


    private Set<Insured> populateFamilyId(Set<Insured> insureds) {
        Map<String, Object> entitySequenceMap = sequenceGenerator.getEntitySequenceMap(Insured.class);
        final Integer[] sequenceNumber = {((Integer) entitySequenceMap.get("sequenceNumber")) + 1};
        insureds.forEach(insured -> {
            if (insured.getNoOfAssured() == null && insured.getCategory()!=null) {
                String selfFamilySequence = sequenceNumber[0] + "01";
                sequenceNumber[0] = sequenceNumber[0] + 1;
                FamilyId familyId = new FamilyId(selfFamilySequence);
                insured = insured.updateWithFamilyId(familyId);
            }
            if (isNotEmpty(insured.getInsuredDependents())) {
                Map<Relationship, List<Integer>> relationshipSequenceMap = groupDependentSequenceByRelation(insured.getInsuredDependents());
                insured.getInsuredDependents().forEach(insuredDependent -> {
                    if (insuredDependent.getNoOfAssured() == null) {
                        List<Integer> sequenceList = relationshipSequenceMap.get(insuredDependent.getRelationship());
                        String selfFamilySequence = sequenceNumber[0].toString() + sequenceList.get(0);
                        sequenceList.remove(0);
                        sequenceNumber[0] = sequenceNumber[0] + 1;
                        relationshipSequenceMap.put(insuredDependent.getRelationship(), sequenceList);
                        FamilyId familyId = new FamilyId(selfFamilySequence);
                        insuredDependent = insuredDependent.updateWithFamilyId(familyId);
                    }
                });
            }
        });
        sequenceGenerator.updateSequence(sequenceNumber[0], (Integer) entitySequenceMap.get("sequenceId"));
        return insureds;
    }

    private Map<Relationship, List<Integer>> groupDependentSequenceByRelation(Set<InsuredDependent> insuredDependents) {
        Map<Relationship, List<Integer>> dependentSequenceMap = Maps.newHashMap();
        final int[] currentSequence = {3};
        insuredDependents.forEach(insuredDependent -> {
            if (dependentSequenceMap.get(insuredDependent.getRelationship()) == null) {
                List<Integer> sequenceList = new ArrayList<Integer>();
                sequenceList.add((Relationship.SPOUSE.equals(insuredDependent.getRelationship()) ? 2 : currentSequence[0]));
                currentSequence[0] = currentSequence[0] + 1;
                dependentSequenceMap.put(insuredDependent.getRelationship(), sequenceList);
            } else {
                List<Integer> sequenceList = dependentSequenceMap.get(insuredDependent.getRelationship());
                currentSequence[0] = sequenceList.get((sequenceList.size() - 1));
                currentSequence[0] = currentSequence[0] + 1;
                sequenceList.add(currentSequence[0]);
            }
        });
        return dependentSequenceMap;
    }

}
