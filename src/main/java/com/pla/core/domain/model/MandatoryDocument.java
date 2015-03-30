package com.pla.core.domain.model;

import com.pla.sharedkernel.identifier.CoverageId;
import com.pla.sharedkernel.identifier.PlanId;
import lombok.*;
import org.nthdimenzion.common.crud.ICrudEntity;
import org.nthdimenzion.utils.UtilValidator;

import javax.persistence.*;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * Created by Admin on 3/27/2015.
 */
@Entity
@Table(name = "mandatory_document")
@EqualsAndHashCode(of = {"id"})
@ToString
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class MandatoryDocument implements ICrudEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;

    @Embedded
    private PlanId planId;

    @Embedded
    private CoverageId coverageId;

    @Enumerated(EnumType.STRING)
    private ProcessType process;

    @ElementCollection
    private Set<String> documents;

    private MandatoryDocument(PlanId planId, ProcessType process, Set<String> documents) {
        checkNotNull(planId);
        checkNotNull(process);
        checkState(UtilValidator.isNotEmpty(documents));
        this.planId = planId;
        this.process = process;
        this.documents = documents;
    }

    public static MandatoryDocument createMandatoryDocumentWithCoverageId(PlanId planId, CoverageId coverageId, ProcessType process, Set<String> documents){
        MandatoryDocument mandatoryDocument = new MandatoryDocument(planId,process,documents);
        mandatoryDocument.assignCoverageId(coverageId);
        return mandatoryDocument;
    }

    public static MandatoryDocument createMandatoryDocumentWithPlanId(PlanId planId,ProcessType process, Set<String> documents){
        MandatoryDocument mandatoryDocument = new MandatoryDocument(planId,process,documents);
        return mandatoryDocument;
    }

    public MandatoryDocument updateMandatoryDocument(Set<String> documents) {
        checkNotNull(documents);
        this.documents = documents;
        return this;
    }

    private void assignCoverageId(CoverageId coverageId){
        this.coverageId = coverageId;
    }
}
