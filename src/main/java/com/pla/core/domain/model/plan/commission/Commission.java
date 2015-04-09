package com.pla.core.domain.model.plan.commission;

import com.pla.core.domain.exception.CommissionDomainException;
import com.pla.sharedkernel.domain.model.CommissionDesignation;
import com.pla.sharedkernel.domain.model.CommissionTermType;
import com.pla.sharedkernel.domain.model.CommissionType;
import com.pla.sharedkernel.identifier.CommissionId;
import com.pla.sharedkernel.identifier.PlanId;
import lombok.*;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;
import org.nthdimenzion.common.crud.ICrudEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.*;

/**
 * Created by User on 3/31/2015.
 */
@Entity
@Table(name = "commission")
@EqualsAndHashCode(of = {"planId", "availableFor", "fromDate"})
@ToString(of = {"planId", "fromDate"})
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Getter(value = AccessLevel.PACKAGE)
@NamedQuery(name = "findAllCommissionByPlanIdAndDesignationId", query = " FROM Commission WHERE planId=:planId AND availableFor =:availableFor AND thruDate IS NULL")
public class Commission implements ICrudEntity {

    @EmbeddedId
    private CommissionId commissionId;

    private PlanId planId;

    @Enumerated(EnumType.STRING)
    private CommissionDesignation availableFor;

    @Enumerated(EnumType.STRING)
    private CommissionType commissionType;

    @Enumerated(EnumType.STRING)
    private CommissionTermType commissionTermType;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate fromDate;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate thruDate;

    @ElementCollection
    @OrderColumn
    @JoinTable(name = "COMMISSION_COMMISSION_TERM", joinColumns = @JoinColumn(name = "COMMISSION_ID"))
    private Set<CommissionTerm> commissionTerms;

    Commission(CommissionId commissionId, PlanId planId, CommissionDesignation availableFor, CommissionType commissionType, CommissionTermType commissionTermType, LocalDate fromDate) {
        checkNotNull(commissionId);
        checkNotNull(planId);
        checkArgument(availableFor != null);
        checkArgument(commissionType != null);
        checkArgument(commissionTermType != null);
        checkArgument(fromDate != null);
        checkState(fromDate.isAfter(LocalDate.now().minusDays(1)));
        this.commissionId = commissionId;
        this.planId = planId;
        this.availableFor = availableFor;
        this.commissionType = commissionType;
        this.commissionTermType = commissionTermType;
        this.fromDate = fromDate;

    }

    public static Commission createCommission(CommissionId commissionId, PlanId planId, CommissionDesignation availableFor, CommissionType commissionType, CommissionTermType commissionTermType, LocalDate fromDate) {
        return new Commission(commissionId, planId, availableFor, commissionType, commissionTermType, fromDate);
    }

    public boolean validateOverLappingYears(Set<CommissionTerm> commissionTerms) {
        List<CommissionTerm> commissionTermList = new ArrayList(commissionTerms);
        for (int i = 0; i < commissionTermList.size(); i++) {
            final CommissionTerm commissionTermI = commissionTermList.get(i);
            for (int j = 0; j < commissionTermList.size(); j++) {
                final CommissionTerm commissionTermJ = commissionTermList.get(j);
                if (i != j) {
                    if (commissionTermJ.getStartYear() < commissionTermI.getEndYear()) {
                        if (!(commissionTermJ.getEndYear() < commissionTermI.getStartYear())) {
                            return false;
                        }
                    } else {
                        if (!(commissionTermJ.getEndYear() > commissionTermI.getStartYear())) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;

    }

    public Commission updateWithCommissionTerms(Set<CommissionTerm> commissionTerms) {
        if (!validateOverLappingYears(commissionTerms))
            throw new CommissionDomainException("Overlapping Years!!");
        this.commissionTerms = commissionTerms;
        return this;
    }

    public Commission expireCommission(LocalDate expireDate) {
        this.thruDate = expireDate;
        return this;
    }

    public Commission addCommissionTerm(Set<CommissionTerm> commissionTerms) {
        this.commissionTerms = commissionTerms;
        if (!validateOverLappingYears(commissionTerms))
            throw new CommissionDomainException("Overlapping Years!!");

        return this;


    }
}



