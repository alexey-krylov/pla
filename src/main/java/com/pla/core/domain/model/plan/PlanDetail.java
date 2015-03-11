package com.pla.core.domain.model.plan;

import com.pla.sharedkernel.domain.model.ClientType;
import com.pla.sharedkernel.domain.model.PlanType;
import com.pla.sharedkernel.domain.model.Relationship;
import com.pla.sharedkernel.identifier.LineOfBusinessId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.ToString;
import org.joda.time.LocalDate;
import org.nthdimenzion.utils.UtilValidator;

import java.util.Set;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author: pradyumna
 * @since 1.0 11/03/2015
 */
@Getter(AccessLevel.PACKAGE)
@ToString
public class PlanDetail {

    private String planName;
    private String planCode;
    private LocalDate launchDate;
    private LocalDate withdrawalDate;
    private int freeLookPeriod = 15;
    private int minEntryAge;
    private int maxEntryAge;
    private boolean taxApplicable;
    private int surrenderAfter;
    private Set<Relationship> applicableRelationships;
    private Set<EndorsementType> endorsementTypes;
    private LineOfBusinessId lineOfBusinessId;
    private PlanType planType;
    private ClientType clientType;

    private PlanDetail(final PlanDetailBuilder planDetailBuilder) {
        checkArgument(UtilValidator.isNotEmpty(planDetailBuilder.planName), "Plan Name cannot be empty.");
        this.planName = planDetailBuilder.planName;

        checkArgument(planDetailBuilder.planCode != null);
        this.planCode = planDetailBuilder.planCode;

        checkArgument(planDetailBuilder.minEntryAge > 0, "Min Entry Age cannot be less than 0");
        this.minEntryAge = planDetailBuilder.minEntryAge;

        checkArgument(planDetailBuilder.maxEntryAge > planDetailBuilder.minEntryAge, "Max Entry Age cannot be less than Min Entry Age");
        this.maxEntryAge = planDetailBuilder.maxEntryAge;

        checkArgument(planDetailBuilder.launchDate != null, "Cannot create a Plan with Launch Date");
        checkArgument(planDetailBuilder.launchDate.isAfter(LocalDate.now()), "Cannot create a Plan with Launch Date less than Today's date");

        this.launchDate = planDetailBuilder.launchDate;

        checkArgument(planDetailBuilder.withdrawalDate.isAfter(launchDate), "Withdrawal cannot be less than launchDate");
        this.withdrawalDate = planDetailBuilder.withdrawalDate;


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

    public static class PlanDetailBuilder {

        boolean taxApplicable;
        private String planName;
        private String planCode;
        private LocalDate launchDate;
        private LocalDate withdrawalDate;
        private int freeLookPeriod;
        private int minEntryAge;
        private int maxEntryAge;
        private int surrenderAfterYears;
        private Set<Relationship> applicableRelationships;
        private Set<EndorsementType> endorsementTypes;
        private LineOfBusinessId lineOfBusinessId;
        private PlanType planType;
        private ClientType clientType;

        public PlanDetailBuilder withPlanName(String planName) {
            this.planName = planName;
            return this;
        }

        public PlanDetailBuilder withPlanCode(String planCode) {
            this.planCode = planCode;
            return this;
        }

        public PlanDetailBuilder withLaunchDate(LocalDate launchDate) {
            this.launchDate = launchDate;
            return this;
        }

        public PlanDetailBuilder withWithdrawalDate(LocalDate withdrawalDate) {
            this.withdrawalDate = withdrawalDate;
            return this;
        }

        public PlanDetailBuilder withFreeLookPeriod(int freeLookPeriod) {
            this.freeLookPeriod = freeLookPeriod;
            return this;
        }

        public PlanDetailBuilder withMinEntryAge(int minEntryAge) {
            this.minEntryAge = minEntryAge;
            return this;
        }

        public PlanDetailBuilder withMaxEntryAge(int maxEntryAge) {
            this.maxEntryAge = maxEntryAge;
            return this;
        }

        public PlanDetailBuilder withTaxApplicable(boolean taxApplicable) {
            this.taxApplicable = taxApplicable;
            return this;
        }

        public PlanDetailBuilder withApplicableRelationships(Set<Relationship> relationships) {
            this.applicableRelationships = relationships;
            return this;
        }

        public PlanDetailBuilder withEndorsementTypes(Set<EndorsementType> endorsementTypes) {
            this.endorsementTypes = endorsementTypes;
            return this;
        }

        public PlanDetailBuilder withSurrenderAfter(int surrenderAfterYears) {
            this.surrenderAfterYears = surrenderAfterYears;
            return this;
        }

        public PlanDetailBuilder withLineOfBusinessId(LineOfBusinessId lineOfBusinessId) {
            this.lineOfBusinessId = lineOfBusinessId;
            return this;
        }

        public PlanDetailBuilder withPlanType(PlanType planType) {
            this.planType = planType;
            return this;
        }

        public PlanDetailBuilder withClientType(ClientType clientType) {
            this.clientType = clientType;
            return this;
        }

        public PlanDetail build() {
            return new PlanDetail(this);
        }
    }
}
