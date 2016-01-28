package com.pla.individuallife.endorsement.application.command;

import com.google.common.collect.Sets;
import com.pla.individuallife.endorsement.application.service.ILEndorsementService;
import com.pla.individuallife.endorsement.domain.model.*;
import com.pla.individuallife.endorsement.domain.service.ILEndorsementRoleAdapter;
import com.pla.individuallife.endorsement.domain.service.ILEndorsementRequestNumberGenerator;
import com.pla.individuallife.endorsement.domain.service.IndividualLifeEndorsementService;
import com.pla.individuallife.proposal.presentation.dto.ILProposalMandatoryDocumentDto;
import com.pla.individuallife.sharedresource.model.vo.ILProposerDocument;
import com.pla.individuallife.sharedresource.query.ILClientFinder;
import com.pla.sharedkernel.identifier.EndorsementId;
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
import java.util.*;
import java.util.function.Predicate;

import static org.nthdimenzion.utils.UtilValidator.isEmpty;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Raghu Bandi on 8/27/2015.
 */
@Component
public class ILEndorsementCommandHandler {


    private Repository<IndividualLifeEndorsement> ilEndorsementMongoRepository;

    private IndividualLifeEndorsementService individualLifeEndorsementService;

    //private ILEndorsementNumberGenerator ilEndorsementUniqueNumberGenerator;

    private ILEndorsementRoleAdapter ilEndorsementRoleAdapter;

    private ILEndorsementService ilEndorsementService;

    private ILEndorsementRequestNumberGenerator ilEndorsementRequestNumberGenerator;

    @Autowired
    private ILClientFinder ilFinder;

    @Autowired
    private GridFsTemplate gridFsTemplate;
    @Autowired
    private SequenceGenerator sequenceGenerator;

    @Autowired
    public ILEndorsementCommandHandler(Repository<IndividualLifeEndorsement> ilEndorsementMongoRepository, IndividualLifeEndorsementService individualLifeEndorsementService, ILEndorsementRequestNumberGenerator ilEndorsementRequestNumberGenerator, ILEndorsementRoleAdapter ilEndorsementRoleAdapter, ILEndorsementService ilEndorsementService) {
        this.ilEndorsementMongoRepository = ilEndorsementMongoRepository;
        this.individualLifeEndorsementService = individualLifeEndorsementService;
        this.ilEndorsementRequestNumberGenerator = ilEndorsementRequestNumberGenerator;
        this.ilEndorsementRoleAdapter = ilEndorsementRoleAdapter;
        this.ilEndorsementService = ilEndorsementService;
    }
    @CommandHandler
    public String handle(ILCreateEndorsementCommand ilCreateEndorsementCommand) {
        IndividualLifeEndorsement individualLifeEndorsement = individualLifeEndorsementService.createEndorsement(ilCreateEndorsementCommand.getIlPolicyDto().getPolicyId(), ilCreateEndorsementCommand.getIlPolicyDto(), ilCreateEndorsementCommand.getUserDetails());
        ilEndorsementMongoRepository.add(individualLifeEndorsement);
        return individualLifeEndorsement.getEndorsementRequestNumber();
    }

    @CommandHandler
    public String handle(ILUpdateEndorsementCommand ilUpdateEndorsementCommand) {
        IndividualLifeEndorsement individualLifeEndorsement = individualLifeEndorsementService.updateEndorsement(ilUpdateEndorsementCommand.getILEndorsementDto().getIlPolicyDto().getPolicyId(), ilUpdateEndorsementCommand.getILEndorsementDto(), ilUpdateEndorsementCommand.getUserDetails());
        ilEndorsementMongoRepository.add(individualLifeEndorsement);
        return individualLifeEndorsement.getEndorsementRequestNumber();
    }

/*    @CommandHandler
    public String handle(ILEndorsementCommand ilEndorsementCommand) {
        IndividualLifeEndorsement individualLifeEndorsement = ilEndorsementMongoRepository.load(ilEndorsementCommand.getEndorsementId());
        ILEndorsement ilEndorsement = individualLifeEndorsement.getEndorsement() != null ? individualLifeEndorsement.getEndorsement() : new ILEndorsement();
        ilEndorsement = populateGLEndorsement(ilEndorsement, ilEndorsementCommand.getIlEndorsementInsuredDto(), ilEndorsementCommand.getIlEndorsementType());
        groupLifeEndorsement.updateWithEndorsementDetail(glEndorsement);
        return groupLifeEndorsement.getEndorsementId().getEndorsementId();
    }*/


    /*
    * @TODO change according to type
    * */
/*    private GLEndorsement populateGLEndorsement(ILEndorsement ilEndorsement, ILEndorsementInsuredDto ilEndorsementInsuredDto, ILEndorsementType ilEndorsementType) {
        if (ILEndorsementType.ASSURED_MEMBER_ADDITION.equals(ilEndorsementType)) {
            ILMemberEndorsement ilMemberEndorsement = new ILMemberEndorsement(populateFamilyId(createInsuredDetail(ilEndorsementInsuredDto.getInsureds())));
            ilEndorsement.addMemberEndorsement(ilMemberEndorsement);
        }
        if (ILEndorsementType.MEMBER_PROMOTION.equals(ilEndorsementType)) {
            ILMemberEndorsement ilMemberEndorsement = new ILMemberEndorsement(populateFamilyId(createInsuredDetail(ilEndorsementInsuredDto.getInsureds())));
            ilEndorsement.addPremiumEndorsement(ilMemberEndorsement);
        }
        if (ILEndorsementType.ASSURED_MEMBER_DELETION.equals(ilEndorsementType)) {
            ILMemberEndorsement ilMemberEndorsement = new ILMemberEndorsement(createInsuredDetail(ilEndorsementInsuredDto.getInsureds()));
            ilEndorsement.addMemberDeletionEndorsement(ilMemberEndorsement);
        }
        if (ILEndorsementType.NEW_CATEGORY_RELATION.equals(ilEndorsementType)) {
            ILMemberEndorsement ilMemberEndorsement = new ILMemberEndorsement(populateFamilyId(createInsuredDetail(ilEndorsementInsuredDto.getInsureds())));
            ilEndorsement.addNewCategoryRelationEndorsement(ilMemberEndorsement);
        }
        return ilEndorsement;
    }*/

/*    private List<ILMemberDeletionEndorsement> createGLMemberDeletionEndorsement(ILEndorsementInsuredDto glEndorsementInsuredDto) {
        List<ILMemberDeletionEndorsement> glMemberDeletionEndorsements = glEndorsementInsuredDto.getInsureds().stream().map(new Function<InsuredDto, GLMemberDeletionEndorsement>() {
            @Override
            public ILMemberDeletionEndorsement apply(ProposerDto proposerDto) {
                GLMemberDeletionEndorsement glMemberDeletionEndorsement = new GLMemberDeletionEndorsement(proposerDto.getCategory(),
                        isNotEmpty(insuredDto.getRelationship()) ? Relationship.getRelationship(insuredDto.getRelationship()) : null,
                        insuredDto.getNoOfAssured(), insuredDto.getFamilyId() != null ? new FamilyId(insuredDto.getFamilyId()) : null);
                return ilMemberDeletionEndorsement;
            }
        }).collect(Collectors.toList());
        return glMemberDeletionEndorsements;
    }*/


/*    public Set<PoposerDto> createInsuredDetail(List<PoposerDto> insuredDtos) {
        Set<Insured> insureds = insuredDtos.stream().map(new Function<InsuredDto, Insured>() {
            @Override
            public Insured apply(InsuredDto insuredDto) {
                final InsuredBuilder[] insuredBuilder = {insuredDto.getPlanPremiumDetail()!=null?Insured.getInsuredBuilder(new PlanId(insuredDto.getPlanPremiumDetail().getPlanId()), insuredDto.getPlanPremiumDetail().getPlanCode(), insuredDto.getPlanPremiumDetail().getPremiumAmount(), insuredDto.getPlanPremiumDetail().getSumAssured(), insuredDto.getPlanPremiumDetail().getIncomeMultiplier(),null):
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
    }*/


/*    private Set<InsuredDependent> getInsuredDependent(Set<InsuredDto.InsuredDependentDto> insuredDependentDtos) {
        if (isEmpty(insuredDependentDtos)){
            return Collections.EMPTY_SET;
        }
        Set<InsuredDependent> insuredDependents = insuredDependentDtos.stream().map(new Function<InsuredDto.InsuredDependentDto, InsuredDependent>() {
            @Override
            public InsuredDependent apply(InsuredDto.InsuredDependentDto insuredDependentDto) {
                InsuredDto.PlanPremiumDetailDto premiumDetail = insuredDependentDto.getPlanPremiumDetail();
                final InsuredDependentBuilder[] insuredDependentBuilder = {InsuredDependent.getInsuredDependentBuilder(new PlanId(premiumDetail.getPlanId()), premiumDetail.getPlanCode(), premiumDetail.getPremiumAmount(), Lists.newArrayList(), premiumDetail.getSumAssured())};
                insuredDependentBuilder[0].withCategory(insuredDependentDto.getCategory()).withInsuredName(insuredDependentDto.getSalutation(), insuredDependentDto.getFirstName(), insuredDependentDto.getLastName())
                        .withInsuredNrcNumber(insuredDependentDto.getNrcNumber()).withCompanyName(insuredDependentDto.getCompanyName()).withOccupationClass(insuredDependentDto.getOccupationClass())
                        .withDateOfBirth(insuredDependentDto.getDateOfBirth()).withGender(insuredDependentDto.getGender()).withManNumber(insuredDependentDto.getManNumber()).withFamilyId(insuredDependentDto.getFamilyId())
                        .withRelationship(insuredDependentDto.getRelationship()).withNoOfAssured(insuredDependentDto.getNoOfAssured()).withFamilyId(insuredDependentDto.getFamilyId());
                return insuredDependentBuilder[0].build();
            }
        }).collect(Collectors.toSet());
        return insuredDependents;
    }*/


    @CommandHandler
    public void uploadMandatoryDocument(ILEndorsementDocumentCommand ilEndorsementDocumentCommand) throws IOException {
        IndividualLifeEndorsement individualLifeEndorsement = ilEndorsementMongoRepository.load(new EndorsementId(ilEndorsementDocumentCommand.getEndorsementId()));
        String fileName = ilEndorsementDocumentCommand.getFile() != null ? ilEndorsementDocumentCommand.getFile().getOriginalFilename() : "";
        Set<ILProposerDocument> documents = individualLifeEndorsement.getProposerDocuments();
        if (isEmpty(documents)) {
            documents = Sets.newHashSet();
        }
        String gridFsDocId = gridFsTemplate.store(ilEndorsementDocumentCommand.getFile().getInputStream(), fileName, ilEndorsementDocumentCommand.getFile().getContentType()).getId().toString();
        ILProposerDocument currentDocument = new ILProposerDocument(ilEndorsementDocumentCommand.getDocumentId(), fileName, gridFsDocId, ilEndorsementDocumentCommand.getFile().getContentType(), true, ilEndorsementDocumentCommand.isMandatory());
        currentDocument.setApproved(true);
        if (!documents.add(currentDocument)) {
            ILProposerDocument existingDocument = documents.stream().filter(new Predicate<ILProposerDocument>() {
                @Override
                public boolean test(ILProposerDocument ghProposerDocument) {
                    return currentDocument.equals(ghProposerDocument);
                }
            }).findAny().get();
            gridFsTemplate.delete(new Query(Criteria.where("_id").is(existingDocument.getGridFsDocId())));
            existingDocument = existingDocument.updateWithNameAndContent(ilEndorsementDocumentCommand.getFilename(), gridFsDocId, ilEndorsementDocumentCommand.getFile().getContentType());
        }
        individualLifeEndorsement = individualLifeEndorsement.updateWithDocuments(documents);
    }


    @CommandHandler
    public String submitProposal(SubmitILEndorsementCommand submitILEndorsementCommand) {
        IndividualLifeEndorsement individualLifeEndorsement = ilEndorsementMongoRepository.load(new EndorsementId(submitILEndorsementCommand.getEndorsementId()));
        individualLifeEndorsement = individualLifeEndorsement.submitForApproval(DateTime.now(), submitILEndorsementCommand.getUserDetails().getUsername(), submitILEndorsementCommand.getComment());
        return individualLifeEndorsement.getIdentifier();
    }

    @CommandHandler
    public String returnEndorsement(ReturnILEndorsementCommand returnILEndorsementCommand) {
        IndividualLifeEndorsement individualLifeEndorsement = ilEndorsementMongoRepository.load(new EndorsementId(returnILEndorsementCommand.getEndorsementId()));
        individualLifeEndorsement = individualLifeEndorsement.returnEndorsement(returnILEndorsementCommand.getStatus(), returnILEndorsementCommand.getUserDetails().getUsername(), returnILEndorsementCommand.getComment());
        return individualLifeEndorsement.getIdentifier();
    }

    @CommandHandler
    public String waiveDocumentCommandHandler(ILWaiveMandatoryDocumentCommand cmd) {
        ILEndorsementApprover ilEndorsementApprover =  ilEndorsementRoleAdapter.userToEndorsmentApprover(cmd.getUserDetails());
        IndividualLifeEndorsement individualLifeEndorsement = ilEndorsementMongoRepository.load(new EndorsementId(cmd.getEndorsementId()));
        Set<ILProposerDocument> documents = individualLifeEndorsement.getProposerDocuments();
        if (isEmpty(documents)) {
            documents = Sets.newHashSet();
        }
        for (ILProposalMandatoryDocumentDto proposalDocument : cmd.getWaivedDocuments()){
            ILProposerDocument ilProposerDocument = new ILProposerDocument(proposalDocument.getDocumentId(),proposalDocument.getMandatory(),proposalDocument.getIsApproved());
            documents.add(ilProposerDocument);
        }
        individualLifeEndorsement = ilEndorsementApprover.updateWithDocuments(individualLifeEndorsement,documents);
        return individualLifeEndorsement.getIdentifier();
    }


/*    @CommandHandler
    public String approve(ApproveILEndorsementCommand approveILEndorsementCommand) throws ParseException {
        IndividualLifeEndorsement individualLifeEndorsement = ilEndorsementMongoRepository.load(new EndorsementId(approveILEndorsementCommand.getEndorsementId()));
        if (EndorsementStatus.APPROVED.equals(approveILEndorsementCommand.getStatus()) && !ilEndorsementService.doesAllDocumentWaivesByApprover(approveILEndorsementCommand.getEndorsementId())){
            raiseMandatoryDocumentNotUploaded();
        }
        String endorsementNo = ilEndorsementUniqueNumberGenerator.getEndorsementUniqueNumber(IndividualLifeEndorsement.class);
        EndorsementUniqueNumber endorseNumber = new EndorsementUniqueNumber(endorsementNo);
        Map policyMap = ilFinder.findPolicyById(individualLifeEndorsement.getPolicy().getPolicyId().getPolicyId());
        PremiumDetail premiumDetail = (PremiumDetail) policyMap.get("premiumDetail");
        Industry industry = (Industry) policyMap.get("industry");
        PremiumDetailDto premiumDetailDto = new PremiumDetailDto(premiumDetail.getAddOnBenefit(),premiumDetail.getProfitAndSolvency(),premiumDetail.getHivDiscount(),
                premiumDetail.getValuedClientDiscount(),premiumDetail.getLongTermDiscount(),premiumDetail.getPolicyTermValue());
        individualLifeEndorsement = individualLifeEndorsementService.populateAnnualBasicPremiumOfInsured(individualLifeEndorsement, approveILEndorsementCommand.getUserDetails(), premiumDetailDto, industry);
        individualLifeEndorsement = individualLifeEndorsement.approve(DateTime.now(), approveILEndorsementCommand.getUserDetails().getUsername(), approveILEndorsementCommand.getComment(), endorseNumber);
        return individualLifeEndorsement.getIdentifier().getEndorsementId();
    }*/


/*    private Set<Insured> populateFamilyId(Set<Insured> insureds) {
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
    }*/

/*    private Map<Relationship, List<Integer>> groupDependentSequenceByRelation(Set<InsuredDependent> insuredDependents) {
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
    }*/

}
