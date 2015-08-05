package com.pla.individuallife.proposal.service;

import com.google.common.collect.Lists;
import com.mongodb.gridfs.GridFSDBFile;
import com.pla.individuallife.proposal.domain.model.*;
import com.pla.individuallife.proposal.presentation.dto.ILProposalMandatoryDocumentDto;
import com.pla.individuallife.proposal.presentation.dto.ProposalApproverCommentsDto;
import com.pla.individuallife.proposal.query.ILProposalFinder;
import com.pla.individuallife.proposal.repository.ILProposalStatusAuditRepository;
import com.pla.individuallife.quotation.query.ILQuotationDto;
import com.pla.individuallife.quotation.query.ILQuotationFinder;
import com.pla.individuallife.sharedresource.model.vo.ILProposerDocument;
import com.pla.individuallife.sharedresource.model.vo.ILRiderDetail;
import com.pla.individuallife.sharedresource.model.vo.ProposalPlanDetail;
import com.pla.individuallife.sharedresource.model.vo.ProposedAssured;
import com.pla.publishedlanguage.dto.ClientDocumentDto;
import com.pla.publishedlanguage.dto.SearchDocumentDetailDto;
import com.pla.publishedlanguage.dto.UnderWriterRoutingLevelDetailDto;
import com.pla.publishedlanguage.underwriter.contract.IUnderWriterAdapter;
import com.pla.sharedkernel.domain.model.ProcessType;
import com.pla.sharedkernel.domain.model.RoutingLevel;
import com.pla.sharedkernel.identifier.CoverageId;
import com.pla.sharedkernel.identifier.PlanId;
import com.pla.sharedkernel.identifier.ProposalId;
import com.pla.underwriter.domain.model.UnderWriterDocument;
import com.pla.underwriter.domain.model.UnderWriterInfluencingFactor;
import com.pla.underwriter.domain.model.UnderWriterRoutingLevel;
import com.pla.underwriter.exception.UnderWriterException;
import com.pla.underwriter.finder.UnderWriterFinder;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.Years;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.nthdimenzion.utils.UtilValidator.isEmpty;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Admin on 7/31/2015.
 */
@Service
public class ILProposalService {


    @Autowired
    private ILQuotationFinder quotationFinder;

    @Autowired
    private ILProposalFinder proposalFinder;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private IUnderWriterAdapter underWriterAdapter;

    @Autowired
    private UnderWriterFinder underWriterFinder;

    @Autowired
    private ILProposalStatusAuditRepository ilProposalStatusAuditRepository;

    public boolean hasProposalForQuotation(String quotationId){
        if (isEmpty(quotationId))
            return false;
        ILQuotationDto dto = quotationFinder.getQuotationById(quotationId);
        Map proposalMap = proposalFinder.findProposalByQuotationNumber(dto.getQuotationNumber());
        if (isNotEmpty(proposalMap))
            return true;
        return false;
    }

    public Set<ILProposalMandatoryDocumentDto> findAdditionalDocuments(String proposalId) {
        Map proposal = proposalFinder.getProposalByProposalId(proposalId);
        List<ILProposerDocument> uploadedDocuments = proposal.get("proposalDocuments") != null ? (List<ILProposerDocument>) proposal.get("proposalDocuments") : Lists.newArrayList();
        if (isNotEmpty(uploadedDocuments)) {
            return uploadedDocuments.stream().filter(uploadedDocument -> !uploadedDocument.isMandatory()).map(new Function<ILProposerDocument, ILProposalMandatoryDocumentDto>() {
                @Override
                public ILProposalMandatoryDocumentDto apply(ILProposerDocument ghProposerDocument) {
                    ILProposalMandatoryDocumentDto mandatoryDocumentDto = new ILProposalMandatoryDocumentDto(ghProposerDocument.getDocumentId(), ghProposerDocument.getDocumentName());
                    GridFSDBFile gridFSDBFile = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(ghProposerDocument.getGridFsDocId())));
                    mandatoryDocumentDto.setFileName(gridFSDBFile.getFilename());
                    mandatoryDocumentDto.setContentType(gridFSDBFile.getContentType());
                    mandatoryDocumentDto.setGridFsDocId(gridFSDBFile.getId().toString());
                    try {
                        mandatoryDocumentDto.updateWithContent(IOUtils.toByteArray(gridFSDBFile.getInputStream()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return mandatoryDocumentDto;
                }
            }).collect(Collectors.toSet());
        }
        return Collections.EMPTY_SET;
    }


    public List<ILProposalMandatoryDocumentDto> findMandatoryDocuments(String proposalId) {
        Map proposal = proposalFinder.getProposalByProposalId(proposalId);
        if (isEmpty(proposal))
            return Collections.EMPTY_LIST;
        List<ILProposerDocument> uploadedDocuments = proposal.get("proposalDocuments") != null ? (List<ILProposerDocument>) proposal.get("proposalDocuments") : Lists.newArrayList();
        ProposalPlanDetail planDetail = (ProposalPlanDetail) proposal.get("proposalPlanDetail");
        UnderWriterRoutingLevelDetailDto routingLevelDetailDto = new UnderWriterRoutingLevelDetailDto(new PlanId(planDetail.getPlanId()), LocalDate.now(), ProcessType.ENROLLMENT.name());
        DateTime dob = new DateTime(((ProposedAssured) proposal.get("proposedAssured")).getDateOfBirth());
        Integer age = Years.yearsBetween(dob, DateTime.now()).getYears() + 1;
        RoutingLevel routinglevel = findRoutingLevel(routingLevelDetailDto, proposalId, age);
        List<ClientDocumentDto> mandatoryDocuments = new ArrayList<ClientDocumentDto>();
        if (routinglevel != null) {
            mandatoryDocuments = findClientDocument(routingLevelDetailDto, proposal, age);
        } else {
            List<SearchDocumentDetailDto> documentDetailDtos = Lists.newArrayList();
            SearchDocumentDetailDto searchDocumentDetailDto = new SearchDocumentDetailDto(new PlanId(planDetail.getPlanId()));
            documentDetailDtos.add(searchDocumentDetailDto);
            List<CoverageId> coverageIds = planDetail.getRiderDetails().stream().map(rider -> new CoverageId(rider.getCoverageId())).collect(Collectors.toList());
            documentDetailDtos.add(new SearchDocumentDetailDto(new PlanId(planDetail.getPlanId()), coverageIds));
            mandatoryDocuments.addAll(underWriterAdapter.getMandatoryDocumentsForApproverApproval(documentDetailDtos, ProcessType.ENROLLMENT));
        }
        if (isNotEmpty(mandatoryDocuments)) {
           return mandatoryDocuments.stream().map(new Function<ClientDocumentDto, ILProposalMandatoryDocumentDto>() {
                @Override
                public ILProposalMandatoryDocumentDto apply(ClientDocumentDto clientDocumentDto) {
                    ILProposalMandatoryDocumentDto mandatoryDocumentDto = new ILProposalMandatoryDocumentDto(clientDocumentDto.getDocumentCode(), clientDocumentDto.getDocumentName());
                    Optional<ILProposerDocument> proposerDocumentOptional = uploadedDocuments.stream().filter(new Predicate<ILProposerDocument>() {
                        @Override
                        public boolean test(ILProposerDocument ilProposerDocument) {
                            return clientDocumentDto.getDocumentCode().equals(ilProposerDocument.getDocumentId());
                        }
                    }).findAny();
                    if (proposerDocumentOptional.isPresent()) {
                        try {
                            if (isNotEmpty(proposerDocumentOptional.get().getGridFsDocId())) {
                                GridFSDBFile gridFSDBFile = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(proposerDocumentOptional.get().getGridFsDocId())));
                                mandatoryDocumentDto.setFileName(gridFSDBFile.getFilename());
                                mandatoryDocumentDto.setContentType(gridFSDBFile.getContentType());
                                mandatoryDocumentDto.setGridFsDocId(gridFSDBFile.getId().toString());
                                mandatoryDocumentDto.updateWithContent(IOUtils.toByteArray(gridFSDBFile.getInputStream()));
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    return mandatoryDocumentDto;
                }
            }).collect(Collectors.toList());
        }
        return Collections.EMPTY_LIST;
    }

    public RoutingLevel findRoutingLevel(UnderWriterRoutingLevelDetailDto routingLevelDetailDto, String proposalId, Integer age) {
        Map proposal = proposalFinder.getProposalByProposalId(proposalId);
        RoutingLevel oldRoutingLevel = null;
        RoutingLevel currentRoutingLevel = null;
        ProposalPlanDetail planDetail = (ProposalPlanDetail) proposal.get("proposalPlanDetail");
        currentRoutingLevel = findRoutingLevel(routingLevelDetailDto, proposal, age);
        for (ILRiderDetail rider : planDetail.getRiderDetails()) {
            routingLevelDetailDto.addCoverage(new CoverageId(rider.getCoverageId()));
            oldRoutingLevel = currentRoutingLevel;
            currentRoutingLevel = findRoutingLevel(routingLevelDetailDto, proposal, age);
            if( oldRoutingLevel!= null && currentRoutingLevel!= null && oldRoutingLevel.ordinal() < currentRoutingLevel.ordinal()) currentRoutingLevel = oldRoutingLevel;
            if(currentRoutingLevel == null && oldRoutingLevel != null) currentRoutingLevel = oldRoutingLevel;
        }
        return  currentRoutingLevel;
    }

    private RoutingLevel findRoutingLevel(UnderWriterRoutingLevelDetailDto routingLevelDetailDto, Map proposal, Integer age) {
        List<UnderWriterRoutingLevelDetailDto.UnderWriterInfluencingFactorItem> underWriterInfluencingFactorItems = new ArrayList<UnderWriterRoutingLevelDetailDto.UnderWriterInfluencingFactorItem>();
        try {
            UnderWriterRoutingLevel underWriterRoutingLevel = underWriterFinder.findUnderWriterRoutingLevel(routingLevelDetailDto);
            //TODO : need to add other influencing items
            for (UnderWriterInfluencingFactor underWriterInfluencingFactor : underWriterRoutingLevel.getUnderWriterInfluencingFactors()) {
                if (underWriterInfluencingFactor.name().equalsIgnoreCase(String.valueOf(UnderWriterInfluencingFactor.AGE)))
                    underWriterInfluencingFactorItems.add(new UnderWriterRoutingLevelDetailDto.UnderWriterInfluencingFactorItem(UnderWriterInfluencingFactor.AGE.name(), age.toString()));
                if (underWriterInfluencingFactor.name().equalsIgnoreCase(String.valueOf(UnderWriterInfluencingFactor.SUM_ASSURED))) {
                    //TODO : Need to add previous sum assured values from the previous policies
                    underWriterInfluencingFactorItems.add(new UnderWriterRoutingLevelDetailDto.UnderWriterInfluencingFactorItem(UnderWriterInfluencingFactor.SUM_ASSURED.name(), (((ProposalPlanDetail) proposal.get("proposalPlanDetail")).getSumAssured().toString())));
                }
            }
            routingLevelDetailDto.setUnderWriterInfluencingFactor(underWriterInfluencingFactorItems);
            return underWriterAdapter.getRoutingLevel(routingLevelDetailDto);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private List<ClientDocumentDto> findClientDocument(UnderWriterRoutingLevelDetailDto routingLevelDetailDto, Map proposal, Integer age) {

        List<ClientDocumentDto> mandatoryDocuments = new ArrayList<ClientDocumentDto>();

        List<UnderWriterRoutingLevelDetailDto.UnderWriterInfluencingFactorItem> underWriterInfluencingFactorItems = new ArrayList<UnderWriterRoutingLevelDetailDto.UnderWriterInfluencingFactorItem>();
        try {
            UnderWriterDocument underWriterDocument = underWriterFinder.getUnderWriterDocumentSetUp(routingLevelDetailDto.getPlanId(), null, LocalDate.now(), ProcessType.ENROLLMENT.name());
            //TODO : need to add other influencing items
            for (UnderWriterInfluencingFactor underWriterInfluencingFactor : underWriterDocument.getUnderWriterInfluencingFactors()) {
                if (underWriterInfluencingFactor.name().equalsIgnoreCase(String.valueOf(UnderWriterInfluencingFactor.AGE)))
                    underWriterInfluencingFactorItems.add(new UnderWriterRoutingLevelDetailDto.UnderWriterInfluencingFactorItem(UnderWriterInfluencingFactor.AGE.name(), age.toString()));
                if (underWriterInfluencingFactor.name().equalsIgnoreCase(String.valueOf(UnderWriterInfluencingFactor.SUM_ASSURED))) {
                    //TODO : Need to add previous sum assured values from the previous policies
                    underWriterInfluencingFactorItems.add(new UnderWriterRoutingLevelDetailDto.UnderWriterInfluencingFactorItem(UnderWriterInfluencingFactor.SUM_ASSURED.name(), (((ProposalPlanDetail) proposal.get("proposalPlanDetail")).getSumAssured().toString())));
                }
            }
            routingLevelDetailDto.setUnderWriterInfluencingFactor(underWriterInfluencingFactorItems);
            mandatoryDocuments.addAll(underWriterAdapter.getDocumentsForUnderWriterApproval(routingLevelDetailDto));

        } catch (UnderWriterException ex) {
            ex.printStackTrace();

        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }

        ProposalPlanDetail planDetail = (ProposalPlanDetail) proposal.get("proposalPlanDetail");
        for (ILRiderDetail rider : planDetail.getRiderDetails()) {
            routingLevelDetailDto.addCoverage(new CoverageId(rider.getCoverageId()));
            try{
                UnderWriterDocument underWriterDocument = underWriterFinder.getUnderWriterDocumentSetUp(routingLevelDetailDto.getPlanId(), routingLevelDetailDto.getCoverageId(), LocalDate.now(), ProcessType.ENROLLMENT.name());
                //TODO : need to add other influencing items
                for (UnderWriterInfluencingFactor underWriterInfluencingFactor : underWriterDocument.getUnderWriterInfluencingFactors()) {
                    if (underWriterInfluencingFactor.name().equalsIgnoreCase(String.valueOf(UnderWriterInfluencingFactor.AGE)))
                        underWriterInfluencingFactorItems.add(new UnderWriterRoutingLevelDetailDto.UnderWriterInfluencingFactorItem(UnderWriterInfluencingFactor.AGE.name(), age.toString()));
                    if (underWriterInfluencingFactor.name().equalsIgnoreCase(String.valueOf(UnderWriterInfluencingFactor.SUM_ASSURED))) {
                        //TODO : Need to add previous sum assured values from the previous policies
                        underWriterInfluencingFactorItems.add(new UnderWriterRoutingLevelDetailDto.UnderWriterInfluencingFactorItem(UnderWriterInfluencingFactor.SUM_ASSURED.name(), (((ProposalPlanDetail) proposal.get("proposalPlanDetail")).getSumAssured().toString())));
                    }
                }
                routingLevelDetailDto.setUnderWriterInfluencingFactor(underWriterInfluencingFactorItems);
                mandatoryDocuments.addAll(underWriterAdapter.getDocumentsForUnderWriterApproval(routingLevelDetailDto));
            } catch (UnderWriterException ex) {
                ex.printStackTrace();

            } catch (IllegalArgumentException ex) {
                ex.printStackTrace();
            }
        }

        return mandatoryDocuments;
    }

    public List<ProposalApproverCommentsDto> findApproverComments(String proposalId) {
        List<ILProposalStatusAudit> audits = ilProposalStatusAuditRepository.findByProposalId(new ProposalId(proposalId));
        List<ProposalApproverCommentsDto> proposalApproverCommentsDtos = Lists.newArrayList();
        if (isNotEmpty(audits)) {
            proposalApproverCommentsDtos = audits.stream().map(new Function<ILProposalStatusAudit, ProposalApproverCommentsDto>() {
                @Override
                public ProposalApproverCommentsDto apply(ILProposalStatusAudit ilProposalStatusAudit) {
                    ProposalApproverCommentsDto proposalApproverCommentsDto = new ProposalApproverCommentsDto();
                    try {
                        BeanUtils.copyProperties(proposalApproverCommentsDto, ilProposalStatusAudit);
                        proposalApproverCommentsDto.setStatus(proposalApproverCommentsDto.getProposalStatus().getDescription());
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                    return proposalApproverCommentsDto;
                }
            }).collect(Collectors.toList());
        }
        return proposalApproverCommentsDtos;
    }

}
