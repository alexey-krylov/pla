package com.pla.grouplife.proposal.application.command;

import com.google.common.collect.Sets;
import com.pla.grouphealth.proposal.application.command.GHProposalDocumentRemoveCommand;
import com.pla.grouphealth.proposal.domain.model.GroupHealthProposal;
import com.pla.grouphealth.sharedresource.model.vo.GHProposerDocument;
import com.pla.grouplife.proposal.application.service.GLProposalService;
import com.pla.grouplife.proposal.domain.model.GLProposalApprover;
import com.pla.grouplife.proposal.domain.model.GLProposalProcessor;
import com.pla.grouplife.proposal.presentation.dto.GLProposalMandatoryDocumentDto;
import com.pla.grouplife.sharedresource.model.vo.GLProposerDocument;
import com.pla.grouplife.proposal.domain.model.GroupLifeProposal;
import com.pla.grouplife.proposal.domain.service.GroupLifeProposalRoleAdapter;
import com.pla.grouplife.proposal.domain.service.GroupLifeProposalService;
import com.pla.grouplife.proposal.query.GLProposalFinder;
import com.pla.grouplife.proposal.repository.GlProposalRepository;
import com.pla.grouplife.sharedresource.dto.InsuredDto;
import com.pla.grouplife.sharedresource.dto.PremiumDetailDto;
import com.pla.grouplife.sharedresource.model.vo.Insured;
import com.pla.grouplife.sharedresource.query.GLFinder;
import com.pla.grouplife.sharedresource.util.GLInsuredFactory;
import com.pla.grouplife.sharedresource.util.GroupLifeProposalFactory;
import com.pla.sharedkernel.identifier.ProposalId;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.axonframework.repository.Repository;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.nthdimenzion.utils.UtilValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.nthdimenzion.utils.UtilValidator.isEmpty;

/**
 * Created by User on 6/25/2015.
 */
@Component
public class GroupLifeProposalCommandHandler {

    private GLFinder glFinder;

    private GLProposalFinder glProposalFinder;

    private GroupLifeProposalRoleAdapter groupLifeProposalRoleAdapter;

    private GroupLifeProposalService groupLifeProposalService;

    @Autowired
    private GLProposalService glProposalService;

    private Repository<GroupLifeProposal> groupLifeProposalRepository;

    private GLInsuredFactory glInsuredFactory;

    private GroupLifeProposalFactory groupLifeProposalFactory;

    @Autowired
    private GlProposalRepository glProposalRepository;

    @Autowired
    private GridFsTemplate gridFsTemplate;


    @Autowired
    GroupLifeProposalCommandHandler(GroupLifeProposalService groupLifeProposalService, GLFinder glFinder,
                                    Repository<GroupLifeProposal> groupLifeProposalRepository, GLInsuredFactory glInsuredFactory, GLProposalFinder glProposalFinder, GroupLifeProposalRoleAdapter groupLifeProposalRoleAdapte, GroupLifeProposalFactory groupLifeProposalFactory) {
        this.groupLifeProposalService = groupLifeProposalService;
        this.groupLifeProposalRepository = groupLifeProposalRepository;
        this.glInsuredFactory = glInsuredFactory;
        this.glFinder = glFinder;
        this.glProposalFinder = glProposalFinder;
        this.groupLifeProposalFactory = groupLifeProposalFactory;
        this.groupLifeProposalRoleAdapter = groupLifeProposalRoleAdapte;
    }

    @CommandHandler
    public String createProposal(GLQuotationToProposalCommand glQuotationToProposalCommand) {
        Map quotationMap = glFinder.getQuotationById(glQuotationToProposalCommand.getQuotationId());
        String quotationNumber = (String) quotationMap.get("quotationNumber");
        Map proposalMap = glProposalFinder.findProposalByQuotationNumber(quotationNumber);
        GroupLifeProposal groupLifeProposal = null;
        ProposalId proposalId = new ProposalId(ObjectId.get().toString());
        if (proposalMap != null) {
            proposalId = new ProposalId(proposalMap.get("_id").toString());
            groupLifeProposal = groupLifeProposalRepository.load(proposalId);
        }
        GLProposalProcessor glProposalProcessor = groupLifeProposalRoleAdapter.userToProposalProcessor(glQuotationToProposalCommand.getUserDetails());
        GroupLifeProposal newGroupLifeProposal = glProposalProcessor.createProposal(glQuotationToProposalCommand.getQuotationId(), proposalId, groupLifeProposalFactory);
        groupLifeProposal = groupLifeProposal != null ? newGroupLifeProposal.copyTo(groupLifeProposal) : newGroupLifeProposal;
        if (proposalMap == null) {
            groupLifeProposalRepository.add(groupLifeProposal);
        }
        return groupLifeProposal.getIdentifier().getProposalId();
    }

    @CommandHandler
    public String updateWithAgentId(UpdateGLProposalWithAgentCommand updateGLProposalWithAgentCommand) {
        GroupLifeProposal groupLifeProposal = groupLifeProposalRepository.load(new ProposalId(updateGLProposalWithAgentCommand.getProposalId()));
        groupLifeProposal = groupLifeProposalService.updateWithAgent(groupLifeProposal, updateGLProposalWithAgentCommand.getAgentId(), updateGLProposalWithAgentCommand.getUserDetails(),updateGLProposalWithAgentCommand.getAgentCommissionPercentage(),updateGLProposalWithAgentCommand.getIsCommissionOverridden());
        groupLifeProposalRepository.add(groupLifeProposal);
        return groupLifeProposal.getIdentifier().getProposalId();
    }

    @CommandHandler
    public String updateProposalWithProposerDetail(UpdateGLProposalWithProposerCommand updateGLProposalWithProposerCommand) {
        GroupLifeProposal groupLifeProposal = groupLifeProposalRepository.load(new ProposalId(updateGLProposalWithProposerCommand.getProposalId()));
        groupLifeProposal = groupLifeProposalService.updateWithProposerDetail(groupLifeProposal, updateGLProposalWithProposerCommand.getProposerDto(), updateGLProposalWithProposerCommand.getUserDetails());
        groupLifeProposalRepository.add(groupLifeProposal);
        return groupLifeProposal.getIdentifier().getProposalId();
    }

    @CommandHandler
    public String updateWithInsureDetail(UpdateGLProposalWithInsuredCommand updateGLProposalWithInsuredCommand) {
        List<InsuredDto> insuredDtos = updateGLProposalWithInsuredCommand.getInsuredDtos();
        Set<Insured> insureds = glInsuredFactory.createInsuredDetail(insuredDtos);
        GroupLifeProposal groupLifeQuotation = groupLifeProposalRepository.load(new ProposalId(updateGLProposalWithInsuredCommand.getProposalId()));
        groupLifeQuotation = groupLifeProposalService.updateInsured(groupLifeQuotation, insureds, updateGLProposalWithInsuredCommand.getUserDetails());
        PremiumDetailDto premiumDetailDto = new PremiumDetailDto(BigDecimal.valueOf(20), 365, BigDecimal.valueOf(5), BigDecimal.valueOf(5), BigDecimal.valueOf(5));
        groupLifeQuotation = groupLifeQuotation.updateFlagSamePlanForAllRelation(updateGLProposalWithInsuredCommand.isSamePlanForAllRelation());
        groupLifeQuotation = groupLifeQuotation.updateFlagSamePlanForAllCategory(updateGLProposalWithInsuredCommand.isSamePlanForAllCategory());
        groupLifeQuotation = groupLifeProposalService.updateWithPremiumDetail(groupLifeQuotation, premiumDetailDto, updateGLProposalWithInsuredCommand.getUserDetails());
        groupLifeProposalRepository.add(groupLifeQuotation);
        return groupLifeQuotation.getIdentifier().getProposalId();
    }

    @CommandHandler
    public String updateWithPremiumDetail(UpdateGLProposalWithPremiumDetailCommand updateGLProposalWithPremiumDetailCommand) {
        GroupLifeProposal groupLifeProposal = groupLifeProposalRepository.load(new ProposalId(updateGLProposalWithPremiumDetailCommand.getProposalId()));
        groupLifeProposal = populateAnnualBasicPremiumOfInsured(groupLifeProposal, updateGLProposalWithPremiumDetailCommand.getUserDetails(), updateGLProposalWithPremiumDetailCommand.getPremiumDetailDto());
        groupLifeProposalRepository.add(groupLifeProposal);
        return groupLifeProposal.getIdentifier().getProposalId();
    }

    @CommandHandler
    public GroupLifeProposal recalculatePremium(GLRecalculatedInsuredPremiumCommand glRecalculatedInsuredPremiumCommand) {
        GroupLifeProposal groupHealthProposal = glProposalRepository.findOne(new ProposalId(glRecalculatedInsuredPremiumCommand.getProposalId()));
        groupHealthProposal = populateAnnualBasicPremiumOfInsured(groupHealthProposal, glRecalculatedInsuredPremiumCommand.getUserDetails(), glRecalculatedInsuredPremiumCommand.getPremiumDetailDto());
        return groupHealthProposal;
    }

    @CommandHandler
    public String submitProposal(SubmitGLProposalCommand submitGLProposalCommand) {
        GroupLifeProposal groupLifeProposal = groupLifeProposalRepository.load(new ProposalId(submitGLProposalCommand.getProposalId()));
        groupLifeProposal = groupLifeProposal.submitForApproval(DateTime.now(), submitGLProposalCommand.getUserDetails().getUsername(), submitGLProposalCommand.getComment());
        List<GLProposalMandatoryDocumentDto> glProposalMandatoryDocumentDtos = glProposalService.findMandatoryDocuments(submitGLProposalCommand.getProposalId());
        Set<GLProposerDocument> proposerDocuments  = groupLifeProposal.getProposerDocuments();
        Set<String> documentIds = proposerDocuments.parallelStream().map(new Function<GLProposerDocument, String>() {
            @Override
            public String apply(GLProposerDocument glProposerDocument) {
                return glProposerDocument.getDocumentId();
            }
        }).collect(Collectors.toSet());
        glProposalMandatoryDocumentDtos.parallelStream().filter(new Predicate<GLProposalMandatoryDocumentDto>() {
            @Override
            public boolean test(GLProposalMandatoryDocumentDto glProposalMandatoryDocumentDto) {
                return !documentIds.contains(glProposalMandatoryDocumentDto.getDocumentId());
            }
        }).map(new Function<GLProposalMandatoryDocumentDto, GLProposerDocument>() {
            @Override
            public GLProposerDocument apply(GLProposalMandatoryDocumentDto glProposalMandatoryDocumentDto) {
                proposerDocuments.add(new GLProposerDocument(glProposalMandatoryDocumentDto.getDocumentId(),true,false));
                return null;
            }
        }).collect(Collectors.toSet());
        groupLifeProposal = groupLifeProposal.updateWithDocuments(proposerDocuments);
        return groupLifeProposal.getIdentifier().getProposalId();
    }

    @CommandHandler
    public String proposalApproval(GLProposalApprovalCommand glProposalApprovalCommand) {
        GroupLifeProposal groupLifeProposal = groupLifeProposalRepository.load(new ProposalId(glProposalApprovalCommand.getProposalId()));
        GLProposalApprover glProposalApprover = groupLifeProposalRoleAdapter.userToProposalApprover(glProposalApprovalCommand.getUserDetails());
        groupLifeProposal = glProposalApprover.submitApproval(DateTime.now(), glProposalApprovalCommand.getComment(), groupLifeProposal, glProposalApprovalCommand.getStatus());
        return groupLifeProposal.getIdentifier().getProposalId();
    }


    @CommandHandler
    public void uploadMandatoryDocument(GLProposalDocumentCommand glProposalDocumentCommand) throws IOException {
        GroupLifeProposal groupLifeProposal = groupLifeProposalRepository.load(new ProposalId(glProposalDocumentCommand.getProposalId()));
        String fileName = glProposalDocumentCommand.getFile() != null ? glProposalDocumentCommand.getFile().getOriginalFilename() : "";
        Set<GLProposerDocument> documents = groupLifeProposal.getProposerDocuments();
        if (isEmpty(documents)) {
            documents = Sets.newHashSet();
        }
        String gridFsDocId = gridFsTemplate.store(glProposalDocumentCommand.getFile().getInputStream(), fileName, glProposalDocumentCommand.getFile().getContentType()).getId().toString();
        GLProposerDocument currentDocument = new GLProposerDocument(glProposalDocumentCommand.getDocumentId(), fileName, gridFsDocId, glProposalDocumentCommand.getFile().getContentType(), glProposalDocumentCommand.isMandatory());
        if (!documents.add(currentDocument)) {
            GLProposerDocument existingDocument = documents.stream().filter(new Predicate<GLProposerDocument>() {
                @Override
                public boolean test(GLProposerDocument ghProposerDocument) {
                    return currentDocument.equals(ghProposerDocument);
                }
            }).findAny().get();
            gridFsTemplate.delete(new Query(Criteria.where("_id").is(existingDocument.getGridFsDocId())));
            existingDocument = existingDocument.updateWithNameAndContent(glProposalDocumentCommand.getFilename(), gridFsDocId, glProposalDocumentCommand.getFile().getContentType());
        }
        groupLifeProposal = groupLifeProposal.updateWithDocuments(documents);
    }

    @CommandHandler
    public boolean removeGLProposalAdditionalDocument(GLProposalDocumentRemoveCommand glProposalDocumentRemoveCommand) {
        boolean result = Boolean.FALSE;
        GroupLifeProposal groupLifeProposal = groupLifeProposalRepository.load(new ProposalId(glProposalDocumentRemoveCommand.getProposalId()));
        if(groupLifeProposal != null){
            Set<GLProposerDocument> glProposerDocuments = groupLifeProposal.getProposerDocuments();
            result =  removeDocumentByGridFsDocId(glProposerDocuments, glProposalDocumentRemoveCommand.getGridFsDocId());
        }
        return result;
    }

    private boolean removeDocumentByGridFsDocId(Set<GLProposerDocument> ghProposerDocuments, String gridFsDocId) {
        if(UtilValidator.isNotEmpty(ghProposerDocuments)) {
            for (Iterator iterator = ghProposerDocuments.iterator(); iterator.hasNext(); ) {
                GLProposerDocument glProposerDocument = (GLProposerDocument) iterator.next();
                if (glProposerDocument.getGridFsDocId().equals(gridFsDocId)) {
                    iterator.remove();
                    return Boolean.TRUE;
                }
            }
        }
        return Boolean.FALSE;
    }
    private GroupLifeProposal populateAnnualBasicPremiumOfInsured(GroupLifeProposal groupLifeQuotation, UserDetails userDetails, PremiumDetailDto premiumDetailDto) {
        Set<Insured> insureds = groupLifeQuotation.getInsureds();
        insureds = glInsuredFactory.recalculateProratePremiumForInsureds(premiumDetailDto, insureds);
        groupLifeQuotation = groupLifeProposalService.updateInsured(groupLifeQuotation, insureds, userDetails);
        groupLifeQuotation = groupLifeProposalService.updateWithPremiumDetail(groupLifeQuotation, premiumDetailDto, userDetails);
        return groupLifeQuotation;
    }


}
