package com.pla.core.domain.query;

import com.pla.sharedkernel.domain.model.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;
import org.nthdimenzion.common.crud.ICrudEntity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Set;

/**
 * @author: pradyumna
 * @since 1.0 19/03/2015
 */
@Entity
@Setter
@Getter
public class PlanEntry implements ICrudEntity {

    @Id
    @Column(name = "plan_id")
    private String identifier;
    private String planName;
    private String planCode;
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate launchDate;
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate withdrawalDate;
    private String lineOfBusinessId;
    private PlanType planType;
    private ClientType clientType;
    @ElementCollection
    @CollectionTable(name = "plan_relationship", joinColumns = {@JoinColumn(name = "plan_id")})
    private Set<Relationship> applicableRelationships;
    @Enumerated(EnumType.STRING)
    private PolicyTermType policyTermType;
    @ElementCollection
    @CollectionTable(name = "plan_term", joinColumns = {@JoinColumn(name = "plan_id")})
    private Set<Integer> policyTerm;
    @Enumerated(EnumType.STRING)
    private PremiumTermType premiumTermType;
    @ElementCollection
    @CollectionTable(name = "plan_term", joinColumns = {@JoinColumn(name = "plan_id")})
    private Set<Integer> premiumTerm;
    @Enumerated(EnumType.STRING)
    private CoverageTermType coverageTermType;
    @ElementCollection
    @CollectionTable(name = "plan_term", joinColumns = {@JoinColumn(name = "plan_id")})
    private Set<Integer> coverageTerm;

    @Enumerated(EnumType.STRING)
    private SumAssuredType sumAssuredType;
    private BigDecimal minSumAssured;
    private BigDecimal maxSumAssured;
    private int multiplesOf;
    @ElementCollection
    @CollectionTable(name = "plan_sum_assured", joinColumns = {@JoinColumn(name = "plan_id")})
    private Set<BigDecimal> sumAssured;

    public PlanEntry() {
    }

}
