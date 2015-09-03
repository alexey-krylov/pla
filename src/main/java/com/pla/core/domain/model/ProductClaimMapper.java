package com.pla.core.domain.model;

import com.pla.sharedkernel.identifier.LineOfBusinessEnum;
import lombok.*;
import org.nthdimenzion.common.crud.ICrudEntity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * Created by Admin on 9/2/2015.
 */
@Entity
@Table(name = "product_claim_mapper")
@EqualsAndHashCode(of = {"productClaimId"})
@ToString
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ProductClaimMapper implements ICrudEntity,Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long productClaimId;

    private String planCode;

    @Enumerated(EnumType.STRING)
    private LineOfBusinessEnum lineOfBusiness;


    @OneToMany(cascade = CascadeType.ALL)
    @Setter(AccessLevel.PACKAGE)
    @JoinColumn(name = "product_claim_id")
    private List<CoverageClaimMapper> coverageClaimMappers;


    public ProductClaimMapper(String planCode,LineOfBusinessEnum lineOfBusinessEnum){
        this.planCode = planCode;
        this.lineOfBusiness = lineOfBusinessEnum;
    }

    public static ProductClaimMapper create(String planCode,LineOfBusinessEnum lineOfBusinessEnum){
        return new ProductClaimMapper(planCode,lineOfBusinessEnum);
    }

    public ProductClaimMapper withCoverageClaimMappers(List<CoverageClaimMapper> coverageClaimMappers){
        this.coverageClaimMappers = coverageClaimMappers;
        return this;
    }

}
