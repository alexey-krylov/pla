package com.pla.core.domain.service;

import com.google.common.collect.Lists;
import com.pla.core.domain.model.Admin;
import com.pla.core.domain.model.MandatoryDocument;
import com.pla.core.domain.model.ProcessType;
import com.pla.core.dto.CoverageDto;
import com.pla.core.dto.MandatoryDocumentDto;
import com.pla.core.query.CoverageFinder;
import com.pla.core.query.MandatoryDocumentFinder;
import com.pla.core.query.PlanFinder;
import com.pla.core.specification.MandatoryDocumentIsAssociatedWithPlan;
import com.pla.sharedkernel.identifier.PlanId;
import org.nthdimenzion.ddd.domain.annotations.DomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.nthdimenzion.utils.UtilValidator.isEmpty;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Admin on 3/27/2015.
 */
@DomainService
public class MandatoryDocumentService {

    private AdminRoleAdapter adminRoleAdapter;
    private MandatoryDocumentFinder mandatoryDocumentFinder;
    private PlanFinder planFinder;
    private CoverageFinder coverageFinder;
    private MandatoryDocumentIsAssociatedWithPlan mandatoryDocumentIsAssociatedWithPlan;

    @Autowired
    public MandatoryDocumentService(AdminRoleAdapter adminRoleAdapter,MandatoryDocumentFinder mandatoryDocumentFinder,PlanFinder planFinder,CoverageFinder coverageFinder,MandatoryDocumentIsAssociatedWithPlan mandatoryDocumentIsAssociatedWithPlan) {
        this.adminRoleAdapter = adminRoleAdapter;
        this.mandatoryDocumentFinder = mandatoryDocumentFinder;
        this.planFinder = planFinder;
        this.coverageFinder = coverageFinder;
        this.mandatoryDocumentIsAssociatedWithPlan = mandatoryDocumentIsAssociatedWithPlan;
    }

    public MandatoryDocument createMandatoryDocument(String planId, String coverageId, ProcessType process, Set<String> documents, UserDetails userDetails){
        Admin admin = adminRoleAdapter.userToAdmin(userDetails);
        MandatoryDocumentDto mandatoryDocumentDto = new MandatoryDocumentDto();
        mandatoryDocumentDto.setPlanId(planId);
        mandatoryDocumentDto.setProcess(process.name());
        boolean isMandatoryDocumentIsAssociatedWithPlan =  mandatoryDocumentIsAssociatedWithPlan.isSatisfiedBy(mandatoryDocumentDto);
        MandatoryDocument mandatoryDocument = admin.createMandatoryDocument(planId, coverageId, process, documents,isMandatoryDocumentIsAssociatedWithPlan);
        return mandatoryDocument;
    }

    public MandatoryDocument updateMandatoryDocument(MandatoryDocument mandatoryDocument, Set<String> documents, UserDetails userDetails){
        Admin admin = adminRoleAdapter.userToAdmin(userDetails);
        MandatoryDocument updateMandatoryDocument = admin.updateMandatoryDocument(mandatoryDocument, documents);
        return updateMandatoryDocument;
    }

    public List<MandatoryDocumentDto> getMandatoryDocuments(){
        List<MandatoryDocumentDto> mandatoryDocumentDtos = mandatoryDocumentFinder.getAllMandatoryDocument();
        if (isEmpty(mandatoryDocumentDtos)) {
            return Lists.newArrayList();
        }
        return transformMandatoryDocument(mandatoryDocumentDtos);
    }

    public List<MandatoryDocumentDto> getMandatoryDocumentById(Long documentId){
        List<MandatoryDocumentDto> mandatoryDocumentDtos = mandatoryDocumentFinder.getMandatoryDocumentById(documentId);
        if (isEmpty(mandatoryDocumentDtos)) {
            return Lists.newArrayList();
        }
        return transformMandatoryDocument(mandatoryDocumentDtos);
    }

    public List<MandatoryDocumentDto> transformMandatoryDocument(List<MandatoryDocumentDto> mandatoryDocumentDtos){
        List<MandatoryDocumentDto> mandatoryDocumentDtoList = Lists.newArrayList();
        for (MandatoryDocumentDto mandatoryDocumentDto : mandatoryDocumentDtos) {
            String planName = planFinder.getPlanName(new PlanId(mandatoryDocumentDto.getPlanId()));
            List<CoverageDto> allCoverages = coverageFinder.getAllCoverage();
            if (isNotEmpty(planName))
                mandatoryDocumentDto.setPlanName(planName);
            mandatoryDocumentDto =  new TransformMandatoryDocumentWithCoverageName(allCoverages).apply(mandatoryDocumentDto);
            mandatoryDocumentDtoList.add(mandatoryDocumentDto);
        }
        return mandatoryDocumentDtoList;
    }

    private class TransformMandatoryDocumentWithCoverageName implements Function<MandatoryDocumentDto,MandatoryDocumentDto> {

        private List<CoverageDto> allCoverages;

        TransformMandatoryDocumentWithCoverageName(List<CoverageDto> allCoverages) {
            this.allCoverages = allCoverages;
        }

        @Override
        public MandatoryDocumentDto apply(MandatoryDocumentDto mandatoryDocumentDto) {
            CoverageDto coverageDto = allCoverages.stream().filter(new Predicate<CoverageDto>() {
                @Override
                public boolean test(CoverageDto coverage) {
                    return coverage.getCoverageId().equals(mandatoryDocumentDto.getCoverageId());
                }
            }).findAny().orElse(new CoverageDto());
            mandatoryDocumentDto.setCoverageName(coverageDto.getCoverageName());
            return mandatoryDocumentDto;
        }
    }

}
