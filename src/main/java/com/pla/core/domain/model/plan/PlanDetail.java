package com.pla.core.domain.model.plan;

import com.pla.sharedkernel.domain.model.ClientType;
import com.pla.sharedkernel.domain.model.EndorsementType;
import com.pla.sharedkernel.domain.model.PlanType;
import com.pla.sharedkernel.domain.model.Relationship;
import com.pla.sharedkernel.identifier.LineOfBusinessId;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.joda.time.LocalDate;
import org.nthdimenzion.utils.UtilValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author: pradyumna
 * @since 1.0 11/03/2015
 */
@Getter
@ToString(exclude = {"logger"})
@EqualsAndHashCode(callSuper = false)
public class PlanDetail {

    private static final Logger logger = LoggerFactory.getLogger(PlanDetail.class);
    String planName;
    String planCode;
    LocalDate launchDate;
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

    PlanDetail() {

    }

    PlanDetail(final PlanDetailBuilder planDetailBuilder) {
        checkArgument(UtilValidator.isNotEmpty(planDetailBuilder.planName), "Plan Name cannot be empty.");
        this.planName = planDetailBuilder.planName;

        checkArgument(planDetailBuilder.planCode != null);
        this.planCode = planDetailBuilder.planCode;

        checkArgument(planDetailBuilder.minEntryAge > 0, "Min Entry Age cannot be less than 0");
        this.minEntryAge = planDetailBuilder.minEntryAge;

        checkArgument(planDetailBuilder.maxEntryAge > planDetailBuilder.minEntryAge, "Max Entry Age cannot be less than Min Entry Age");
        this.maxEntryAge = planDetailBuilder.maxEntryAge;

        checkArgument(planDetailBuilder.launchDate != null, "Cannot create a Plan without Launch Date");
        checkArgument(planDetailBuilder.launchDate.isAfter(LocalDate.now()), "Cannot create a Plan with Launch Date less than Today's date");

        this.launchDate = planDetailBuilder.launchDate;

        if (planDetailBuilder.withdrawalDate != null) {
            checkArgument(planDetailBuilder.withdrawalDate.isAfter(launchDate), "Withdrawal cannot be less than launchDate");
            this.withdrawalDate = planDetailBuilder.withdrawalDate;
        }

        checkArgument(planDetailBuilder.clientType != null, "Cannot create Plan without Client Type");
        this.clientType = planDetailBuilder.clientType;

        checkArgument(UtilValidator.isNotEmpty(planDetailBuilder.endorsementTypes));
        this.endorsementTypes = planDetailBuilder.endorsementTypes;

        if (this.clientType.equals(ClientType.INDIVIDUAL)) {
            this.surrenderAfter = planDetailBuilder.surrenderAfterYears;
            Stream<EndorsementType> groupEndorsementType = this.endorsementTypes.stream().filter(endorsementType ->
                            endorsementType.equals(EndorsementType.MEMBER_ADDITION)
                                    || endorsementType.equals(EndorsementType.MEMBER_DELETION)
                                    || endorsementType.equals(EndorsementType.PROMOTION)
                                    || endorsementType.equals(EndorsementType.NEW_COVER)
            );
            checkArgument(groupEndorsementType.count() == 0, "Group Endorsements are not allowed for Plan with Client Type as %s", this.clientType);
        }

        checkArgument(planDetailBuilder.lineOfBusinessId != null, "Cannot create Plan without Line of Business");
        this.lineOfBusinessId = planDetailBuilder.lineOfBusinessId;

        checkArgument(planDetailBuilder.planType != null, "Cannot create Plan without Plan Type");
        this.planType = planDetailBuilder.planType;

        this.freeLookPeriod = planDetailBuilder.freeLookPeriod;
        this.taxApplicable = planDetailBuilder.taxApplicable;
        this.applicableRelationships = planDetailBuilder.applicableRelationships;

    }

    public static PlanDetailBuilder builder() {
        return new PlanDetailBuilder();
    }

    public String getPlanName() {
        return planName;
    }

    public String getPlanCode() {
        return planCode;
    }

    public LocalDate getLaunchDate() {
        return launchDate;
    }

    public LocalDate getWithdrawalDate() {
        return withdrawalDate;
    }

    public int getFreeLookPeriod() {
        return freeLookPeriod;
    }

    public int getMinEntryAge() {
        return minEntryAge;
    }

    public int getMaxEntryAge() {
        return maxEntryAge;
    }

    public boolean isTaxApplicable() {
        return taxApplicable;
    }

    public int getSurrenderAfter() {
        return surrenderAfter;
    }

    public Set<Relationship> getApplicableRelationships() {
        return applicableRelationships;
    }

    public Set<EndorsementType> getEndorsementTypes() {
        return endorsementTypes;
    }

    public LineOfBusinessId getLineOfBusinessId() {
        return lineOfBusinessId;
    }

    public PlanType getPlanType() {
        return planType;
    }

    public ClientType getClientType() {
        return clientType;
    }

}
