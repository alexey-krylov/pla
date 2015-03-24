package com.pla.core.presentation.command;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.joda.deser.LocalDateDeserializer;
import com.pla.sharedkernel.domain.model.ClientType;
import com.pla.sharedkernel.domain.model.EndorsementType;
import com.pla.sharedkernel.domain.model.PlanType;
import com.pla.sharedkernel.domain.model.Relationship;
import com.pla.sharedkernel.identifier.LineOfBusinessId;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.joda.time.LocalDate;

import java.util.Set;

/**
 * @author: pradyumna
 * @since 1.0 23/03/2015
 */
@Setter
@Getter
@ToString
public class CreatePlanCommand {

    Detail planDetail;
}

@Getter
@Setter
class Detail {
    String planName;
    String planCode;
    @JsonDeserialize(using = LocalDateDeserializer.class)
    LocalDate launchDate;
    @JsonDeserialize(using = LocalDateDeserializer.class)
    LocalDate withdrawalDate;
    int freeLookPeriod = 15;
    int minEntryAge;
    int maxEntryAge;
    boolean taxApplicable;
    int surrenderAfter;
    Set<Relationship> applicableRelationships;
    Set<EndorsementType> endorsementTypes;
    LineOfBusinessId lineOfBusinessId;
    PlanType planType;
    ClientType clientType;
}
