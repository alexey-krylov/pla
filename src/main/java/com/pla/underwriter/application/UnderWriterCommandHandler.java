package com.pla.underwriter.application;

import com.pla.sharedkernel.identifier.CoverageId;
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

    private UnderWriterDocumentRepository underWriterDocumentRepository;

    private UnderWriterRoutingLevelRepository underWriterRoutingLevelRepository;

    private IIdGenerator idGenerator;

    @Autowired
    public UnderWriterCommandHandler(UnderWriterService underWriterService,UnderWriterDocumentRepository underWriterDocumentRepository,UnderWriterRoutingLevelRepository underWriterRoutingLevelRepository, IIdGenerator idGenerator) {
        this.underWriterService = underWriterService;
        this.underWriterDocumentRepository = underWriterDocumentRepository;
        this.underWriterRoutingLevelRepository = underWriterRoutingLevelRepository;
        this.idGenerator = idGenerator;
    }

    @CommandHandler
    public void createUnderWriterDocumentHandler(CreateUnderWriterDocumentCommand createUnderWriterDocumentCommand) {
        UnderWriterDocument underWriterDocument = null;
        if (createUnderWriterDocumentCommand.getUnderWriterDocumentId()==null){
            underWriterDocument = findUnderWriterDocumentSetUp(createUnderWriterDocumentCommand.getPlanCode(),createUnderWriterDocumentCommand.getCoverageId(),createUnderWriterDocumentCommand.getProcessType());
            if(underWriterDocument!=null){
                raiseDuplicateUnderWriterDocumentSetUp();
                return;
            }
        }
        underWriterDocument = findUnderWriterDocumentSetUp(createUnderWriterDocumentCommand.getPlanCode(),createUnderWriterDocumentCommand.getCoverageId(),createUnderWriterDocumentCommand.getProcessType());
        if (underWriterDocument != null) {
            underWriterDocument = underWriterDocument.expireUnderWriterDocument(createUnderWriterDocumentCommand.getEffectiveFrom().minusDays(1));
            underWriterDocumentRepository.save(underWriterDocument);
        }
        UnderWriterDocumentId underWriterDocumentId = new UnderWriterDocumentId(idGenerator.nextId());
        String planCode = createUnderWriterDocumentCommand.getPlanCode();
        CoverageId coverageId = new CoverageId(createUnderWriterDocumentCommand.getCoverageId());
        List<Map<Object,Map<String,Object>>> transformedUnderWriterDocument = underWriterService.transformUnderWriterDocument(createUnderWriterDocumentCommand.getUnderWriterDocumentItems());
        UnderWriterDocument document = isNotEmpty(createUnderWriterDocumentCommand.getCoverageId()) ? UnderWriterDocument.createUnderWriterDocumentWithOptionalCoverage(underWriterDocumentId,planCode,coverageId, createUnderWriterDocumentCommand.getProcessType(), transformedUnderWriterDocument, createUnderWriterDocumentCommand.getUnderWriterInfluencingFactors(), createUnderWriterDocumentCommand.getEffectiveFrom())
                : UnderWriterDocument.createUnderWriterDocumentWithPlan(underWriterDocumentId,planCode, createUnderWriterDocumentCommand.getProcessType(), transformedUnderWriterDocument, createUnderWriterDocumentCommand.getUnderWriterInfluencingFactors(), createUnderWriterDocumentCommand.getEffectiveFrom());
        underWriterDocumentRepository.save(document);
    }

    @CommandHandler
    public void createUnderWriterRoutingLevelHandler(CreateUnderWriterRoutingLevelCommand createUnderWriterRoutingLevelCommand) {
        UnderWriterRoutingLevel underWriterRoutingLevel = null;
        if (isNotEmpty(createUnderWriterRoutingLevelCommand.getPlanCode()) && isNotEmpty(createUnderWriterRoutingLevelCommand.getCoverageId())) {
            underWriterRoutingLevel = underWriterRoutingLevelRepository.findByPlanCodeAndCoverageIdAndValidityTillAndProcessType(createUnderWriterRoutingLevelCommand.getPlanCode(), new CoverageId(createUnderWriterRoutingLevelCommand.getCoverageId()), null, createUnderWriterRoutingLevelCommand.getProcessType().name());
        } else if (isNotEmpty(createUnderWriterRoutingLevelCommand.getPlanCode()) && isEmpty(createUnderWriterRoutingLevelCommand.getCoverageId())) {
            underWriterRoutingLevel = underWriterRoutingLevelRepository.findByPlanCodeAndValidTillAndProcessType(createUnderWriterRoutingLevelCommand.getPlanCode(), null, createUnderWriterRoutingLevelCommand.getProcessType().name());
        }
        if (underWriterRoutingLevel != null) {
            underWriterRoutingLevel = underWriterRoutingLevel.expireUnderWriterRoutingLevel(createUnderWriterRoutingLevelCommand.getEffectiveFrom().minusDays(1));
            underWriterRoutingLevelRepository.save(underWriterRoutingLevel);
        }
        UnderWriterRoutingLevelId underWriterRoutingLevelId = new UnderWriterRoutingLevelId(idGenerator.nextId());
        String planCode = createUnderWriterRoutingLevelCommand.getPlanCode();
        CoverageId coverageId = new CoverageId(createUnderWriterRoutingLevelCommand.getCoverageId());
        UnderWriterRoutingLevel document = isNotEmpty(createUnderWriterRoutingLevelCommand.getCoverageId()) ? UnderWriterRoutingLevel.createUnderWriterRoutingLevelWithOptionalCoverage(underWriterRoutingLevelId, planCode, coverageId, createUnderWriterRoutingLevelCommand.getProcessType(), createUnderWriterRoutingLevelCommand.getUnderWriterDocumentItem(), createUnderWriterRoutingLevelCommand.getUnderWriterInfluencingFactors(), createUnderWriterRoutingLevelCommand.getEffectiveFrom())
                : UnderWriterRoutingLevel.createUnderWriterRoutingLevelWithPlan(underWriterRoutingLevelId, planCode, createUnderWriterRoutingLevelCommand.getProcessType(), createUnderWriterRoutingLevelCommand.getUnderWriterDocumentItem(), createUnderWriterRoutingLevelCommand.getUnderWriterInfluencingFactors(), createUnderWriterRoutingLevelCommand.getEffectiveFrom());
        underWriterRoutingLevelRepository.save(document);
    }

    public UnderWriterDocument findUnderWriterDocumentSetUp(String planCode,String coverageId,UnderWriterProcessType processType){
        UnderWriterDocument underWriterDocument = null;
        if (isNotEmpty(planCode) && isNotEmpty(coverageId) ){
            underWriterDocument = underWriterDocumentRepository.findByPlanCodeAndCoverageIdAndValidityDate(planCode, new CoverageId(coverageId), null, processType.name());
        } else if (isNotEmpty(planCode) && isEmpty(coverageId)) {
            underWriterDocument = underWriterDocumentRepository.findByPlanCodeAndValidityDate(planCode, null, processType.name());
        }
        return underWriterDocument;
    }
}
