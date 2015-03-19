package com.pla.core.domain.model.plan;

import com.pla.sharedkernel.domain.model.ClientType;
import com.pla.sharedkernel.domain.model.PlanType;
import com.pla.sharedkernel.domain.model.Relationship;
import lombok.Getter;
import lombok.Setter;
import org.joda.time.LocalDate;
import org.nthdimenzion.common.crud.ICrudEntity;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
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
    private String identifier;
    private String planName;
    private String planCode;
    private LocalDate launchDate;
    private LocalDate withdrawalDate;
    private String lineOfBusinessId;
    private PlanType planType;
    private ClientType clientType;
    @ElementCollection
    @CollectionTable(name = "plan_relationship")
    private Set<Relationship> applicableRelationships;
    @ElementCollection
    @CollectionTable(name = "plan_endorsementType")
    private Set<EndorsementType> endorsementTypes;

    public PlanEntry() {
    }

}
