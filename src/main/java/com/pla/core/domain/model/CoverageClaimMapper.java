package com.pla.core.domain.model;

import com.pla.sharedkernel.identifier.CoverageId;
import lombok.*;
import org.nthdimenzion.common.crud.ICrudEntity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

/**
 * Created by Admin on 9/2/2015.
 */
@Entity
@Getter
@Setter
@EqualsAndHashCode(of = {"coverageClaimId"})
@NoArgsConstructor
public class CoverageClaimMapper implements ICrudEntity,Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long coverageClaimId;

    @Embedded
    private CoverageId coverageId;

    @ElementCollection
    @CollectionTable(name="coverage_claim_type",
            joinColumns=@JoinColumn(name="coverage_claim_id"))
    @Column(name = "claim_type")
    private Set<String> claimTypes;

    public CoverageClaimMapper(CoverageId coverageId, Set<String> claimTypes){
        this.coverageId = coverageId;
        this.claimTypes = claimTypes;
    }

    public static CoverageClaimMapper create(CoverageId coverageId, Set<String> claimTypes){
        return new CoverageClaimMapper(coverageId,claimTypes);
    }

}
