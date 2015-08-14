package com.pla.underwriter.application;

import com.pla.publishedlanguage.contract.IPlanAdapter;
import com.pla.sharedkernel.identifier.CoverageId;
import com.pla.sharedkernel.identifier.PlanId;
import com.pla.sharedkernel.identifier.UnderWriterDocumentId;
import com.pla.sharedkernel.identifier.UnderWriterRoutingLevelId;
import com.pla.underwriter.domain.model.UnderWriterDocument;
import com.pla.underwriter.domain.model.UnderWriterProcessType;
import com.pla.underwriter.domain.model.UnderWriterRoutingLevel;
import com.pla.underwriter.repository.UnderWriterDocumentRepository;
import com.pla.underwriter.repository.UnderWriterRoutingLevelRepository;
import com.pla.underwriter.service.UnderWriterService;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.nthdimenzion.object.utils.IIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static com.pla.underwriter.exception.UnderWriterException.raiseDuplicateUnderWriterDocumentSetUp;
import static org.nthdimenzion.utils.UtilValidator.isEmpty;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Admin on 5/13/2015.
 */
@Component
public class UnderWriterCommandHandler {

    private UnderWriterService underWriterService;

    private IPlanAdapter iPlanAdapter;

    private UnderWriterDocumentRepository underWriterDocumentRepository;

    private UnderWriterRoutingLevelRepository underWriterRoutingLevelRepository;

    private IIdGenerator idGenerator;

    @Autowired
    public UnderWriterCommandHandler(UnderWriterService underWriterService,UnderWriterDocumentRepository underWriterDocumentRepository,UnderWriterRoutingLevelRepository underWriterRoutingLevelRepository, IIdGenerator idGenerator,IPlanAdapter iPlanAdapter) {
        this.underWriterService = underWriterService;
        this.underWriterDocumentRepository = underWriterDocumentRepository;
        this.underWriterRoutingLevelRepository = underWriterRoutingLevelRepository;
        this.idGenerator = idGenerator;
        this.iPlanAdapter = iPlanAdapter;
    }

    @CommandHandler
    public void createUnderWriterDocumentHandler(CreateUnderWriterDocumentCommand createUnderWriterDocumentCommand) {
        UnderWriterDocument underWriterDocument = null;
        PlanId planId = iPlanAdapter.getPlanId(createUnderWriterDocumentCommand.getPlanCode());
        if (createUnderWriterDocumentCommand.getUnderWriterDocumentId()==null){
            underWriterDocument = findUnderWriterDocumentSetUp(planId,createUnderWriterDocumentCommand.getCoverageId(),createUnderWriterDocumentCommand.getProcessType());
            if(underWriterDocument!=null){
                raiseDuplicateUnderWriterDocumentSetUp();
                return;
            }
        }
        underWriterDocument = findUnderWriterDocumentSetUp(planId,createUnderWriterDocumentCommand.getCoverageId(),createUnderWriterDocumentCommand.getProcessType());
        if (underWriterDocument != null) {
            underWriterDocument = underWriterDocument.expireUnderWriterDocument(createUnderWriterDocumentCommand.getEffectiveFrom().minusDays(1));
            underWriterDocumentRepository.save(underWriterDocument);
        }
        UnderWriterDocumentId underWriterDocumentId = new UnderWriterDocumentId(idGenerator.nextId());
        CoverageId coverageId = new CoverageId(createUnderWriterDocumentCommand.getCoverageId());
        List<Map<Object,Map<String,Object>>> transformedUnderWriterDocument = underWriterService.transformUnderWriterDocument(createUnderWriterDocumentCommand.getUnderWriterDocumentItems());
        UnderWriterDocument document = isNotEmpty(createUnderWriterDocumentCommand.getCoverageId()) ? UnderWriterDocument.createUnderWriterDocumentWithOptionalCoverage(underWriterDocumentId,planId,coverageId, createUnderWriterDocumentCommand.getProcessType(), transformedUnderWriterDocument, createUnderWriterDocumentCommand.getUnderWriterInfluencingFactors(), createUnderWriterDocumentCommand.getEffectiveFrom())
                : UnderWriterDocument.createUnderWriterDocumentWithPlan(underWriterDocumentId,planId, createUnderWriterDocumentCommand.getProcessType(), transformedUnderWriterDocument, createUnderWriterDocumentCommand.getUnderWriterInfluencingFactors(), createUnderWriterDocumentCommand.getEffectiveFrom());
        underWriterDocumentRepository.save(document);
    }

    @CommandHandler
    public void createUnderWriterRoutingLevelHandler(CreateUnderWriterRoutingLevelCommand createUnderWriterRoutingLevelCommand) {
        UnderWriterRoutingLevel underWriterRoutingLevel = null;
        PlanId planId = iPlanAdapter.getPlanId(createUnderWriterRoutingLevelCommand.getPlanCode());
        if (isNotEmpty(createUnderWriterRoutingLevelCommand.getPlanCode()) && isNotEmpty(createUnderWriterRoutingLevelCommand.getCoverageId())) {
            underWriterRoutingLevel = underWriterRoutingLevelRepository.findByPlanCodeAndCoverageIdAndValidityTillAndProcessType(planId, new CoverageId(createUnderWriterRoutingLevelCommand.getCoverageId()), null, createUnderWriterRoutingLevelCommand.getProcessType().name());
        } else if (isNotEmpty(createUnderWriterRoutingLevelCommand.getPlanCode()) && isEmpty(createUnderWriterRoutingLevelCommand.getCoverageId())) {
            underWriterRoutingLevel = underWriterRoutingLevelRepository.findByPlanCodeAndValidTillAndProcessType(planId, null, createUnderWriterRoutingLevelCommand.getProcessType().name());
        }
        if (underWriterRoutingLevel != null) {
            underWriterRoutingLevel = underWriterRoutingLevel.expireUnderWriterRoutingLevel(createUnderWriterRoutingLevelCommand.getEffectiveFrom().minusDays(1));
            underWriterRoutingLevelRepository.save(underWriterRoutingLevel);
        }
        UnderWriterRoutingLevelId underWriterRoutingLevelId = new UnderWriterRoutingLevelId(idGenerator.nextId());
        CoverageId coverageId = new CoverageId(createUnderWriterRoutingLevelCommand.getCoverageId());
        UnderWriterRoutingLevel document = isNotEmpty(createUnderWriterRoutingLevelCommand.getCoverageId()) ? UnderWriterRoutingLevel.createUnderWriterRoutingLevelWithOptionalCoverage(underWriterRoutingLevelId, planId, coverageId, createUnderWriterRoutingLevelCommand.getProcessType(), createUnderWriterRoutingLevelCommand.getUnderWriterDocumentItem(), createUnderWriterRoutingLevelCommand.getUnderWriterInfluencingFactors(), createUnderWriterRoutingLevelCommand.getEffectiveFrom())
                : UnderWriterRoutingLevel.createUnderWriterRoutingLevelWithPlan(underWriterRoutingLevelId, planId, createUnderWriterRoutingLevelCommand.getProcessType(), createUnderWriterRoutingLevelCommand.getUnderWriterDocumentItem(), createUnderWriterRoutingLevelCommand.getUnderWriterInfluencingFactors(), createUnderWriterRoutingLevelCommand.getEffectiveFrom());
        underWriterRoutingLevelRepository.save(document);
    }

    public UnderWriterDocument findUnderWriterDocumentSetUp(PlanId planId,String coverageId,UnderWriterProcessType processType){
        UnderWriterDocument underWriterDocument = null;
        if (planId!=null && isNotEmpty(coverageId) ){
            underWriterDocument = underWriterDocumentRepository.findByPlanCodeAndCoverageIdAndValidityDate(planId, new CoverageId(coverageId), null, processType.name());
        } else if (planId!=null && isEmpty(coverageId)) {
            underWriterDocument = underWriterDocumentRepository.findByPlanCodeAndValidityDate(planId, null, processType.name());
        }
        return underWriterDocument;
    }
}
