package com.pla.core.domain.model;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import com.pla.core.domain.exception.BranchDomainException;
import lombok.*;
import org.hibernate.annotations.Cascade;
import org.joda.time.LocalDate;
import org.nthdimenzion.common.crud.ICrudEntity;

import javax.persistence.*;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Created by User on 3/20/2015.
 */
@Entity
@Table(name = "branch", uniqueConstraints = {@UniqueConstraint(name = "UNQ_BRANCH_CODE_NAME", columnNames = {"branchCode", "branchName"})})
@EqualsAndHashCode(of = {"branchName"})
@ToString(of = {"branchCode", "branchName"})
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Getter(value = AccessLevel.PUBLIC)
public class Branch implements ICrudEntity {

    @EmbeddedId
    private BranchCode branchCode;

    @Embedded
    private BranchName branchName;

    private String currentBranchManager;

    private String currentBranchBdE;
    @ElementCollection(targetClass = BranchManagerFulfillment.class, fetch = FetchType.EAGER)
    @OrderColumn
    @JoinTable(name = "BRANCH_MANAGER_FULFILLMENT", joinColumns = @JoinColumn(name = "BRANCH_CODE"))
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private Set<BranchManagerFulfillment> branchManagerFulfillments = Sets.newHashSet();
    @ElementCollection(targetClass = BranchBdeFulfillment.class, fetch = FetchType.EAGER)
    @OrderColumn
    @JoinTable(name = "BRANCH_BDE_FULFILLMENT", joinColumns = @JoinColumn(name = "BRANCH_CODE"))
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private Set<BranchBdeFulfillment> branchBDEFulfillments = Sets.newHashSet();

    Branch(BranchCode branchCode, BranchName branchName, String currentBranchManager, String currentBranchBdE) {
        Preconditions.checkNotNull(branchCode);
        Preconditions.checkNotNull(branchName);
        this.branchCode = branchCode;
        this.branchName = branchName;
        this.currentBranchManager = currentBranchManager;
        this.currentBranchBdE = currentBranchBdE;
    }

    public Branch assignBranchManager(String employeeId, String firstName, String lastName, LocalDate effectiveFrom) {
        if (currentBranchManager != null) {
            BranchManagerFulfillment currentBranchManagerFulfillment = getBranchManagerFulfillmentForABranchManager(this.currentBranchManager);
            if (currentBranchManager.equals(employeeId)) {
                return this;
            }
            try {
                checkArgument(isNewFulfillmentValid(effectiveFrom, currentBranchManagerFulfillment.getFromDate()));
            } catch (IllegalArgumentException e) {
                throw new BranchDomainException(firstName + " " + lastName + " from date should be greater than " + currentBranchManagerFulfillment.getFromDate().getDayOfMonth() + "/" + currentBranchManagerFulfillment.getFromDate().getMonthOfYear() + "/" + currentBranchManagerFulfillment.getFromDate().getYear());
            }
            expireBranchManager(this.currentBranchManager, effectiveFrom.plusDays(-1));

        }
        BranchManager branchManager = new BranchManager(employeeId, firstName, lastName);
        BranchManagerFulfillment branchManagerFulfillment = new BranchManagerFulfillment(branchManager, effectiveFrom);
        this.branchManagerFulfillments = addBranchManagerFulfillment(employeeId, branchManagerFulfillment);
        return this;
    }

    public Branch assignBranchBDE(String employeeId, String firstName, String lastName, LocalDate effectiveFrom) {
        if (currentBranchBdE != null) {
            BranchBdeFulfillment currentBranchBdeFulfillment = getBranchBDEFulfillmentForABranchBDE(this.currentBranchBdE);
            if (currentBranchBdE.equals(employeeId)) {
                return this;
            }
            try {
                checkArgument(isNewFulfillmentValid(effectiveFrom, currentBranchBdeFulfillment.getFromDate()));
            } catch (IllegalArgumentException e) {
                throw new BranchDomainException(firstName + " " + lastName + " from date should be greater than " + currentBranchBdeFulfillment.getFromDate().getDayOfMonth() + "/" + currentBranchBdeFulfillment.getFromDate().getMonthOfYear() + "/" + currentBranchBdeFulfillment.getFromDate().getYear());
            }
            expireBranchBDE(this.currentBranchBdE, effectiveFrom.plusDays(-1));

        }
        BranchBde branchBde = new BranchBde(employeeId, firstName, lastName);
        BranchBdeFulfillment branchBdeFulfillment = new BranchBdeFulfillment(branchBde, effectiveFrom);
        this.branchBDEFulfillments = addBranchBDEFulfillment(employeeId, branchBdeFulfillment);
        return this;
    }

    public boolean isNewFulfillmentValid(LocalDate newFromDate, LocalDate currentFromDate) {
        return newFromDate.isAfter(currentFromDate);
    }

    public Branch expireBranchManager(String branchManagerId, LocalDate expireDate) {
        this.currentBranchManager = null;
        BranchManagerFulfillment branchManagerFulfillment = getBranchManagerFulfillmentForABranchManager(branchManagerId);
        if (branchManagerFulfillment != null) {
            this.branchManagerFulfillments = expireBranchManagerFulfillment(this.branchManagerFulfillments, branchManagerFulfillment.getBranchManager(), expireDate);
        }
        return this;
    }

    Set<BranchManagerFulfillment> expireBranchManagerFulfillment(Set<BranchManagerFulfillment> branchManagerFulfillments, BranchManager branchManagerToBeExpired, LocalDate expireDate) {
        for (BranchManagerFulfillment branchManagerFulfillment : branchManagerFulfillments) {
            if (branchManagerFulfillment.getBranchManager().equals(branchManagerToBeExpired) && branchManagerFulfillment.getThruDate() == null) {
                branchManagerFulfillment.expireFulfillment(expireDate);
            }
        }
        return branchManagerFulfillments;
    }

    public Branch expireBranchBDE(String branchBDEId, LocalDate expireDate) {
        this.currentBranchBdE = null;
        BranchBdeFulfillment branchBDEFulfillment = getBranchBDEFulfillmentForABranchBDE(branchBDEId);
        if (branchBDEFulfillment != null) {
            this.branchBDEFulfillments = expireBranchBDEFulfillment(this.branchBDEFulfillments, branchBDEFulfillment.getBranchBde(), expireDate);
        }
        return this;
    }


    Set<BranchBdeFulfillment> expireBranchBDEFulfillment(Set<BranchBdeFulfillment> branchBdeFulfillments, BranchBde branchBdeToBeExpired, LocalDate expireDate) {
        for (BranchBdeFulfillment branchBdeFulfillment : branchBdeFulfillments) {
            if (branchBdeFulfillment.getBranchBde().equals(branchBdeToBeExpired) && branchBdeFulfillment.getThruDate() == null) {
                branchBdeFulfillment.expireFulfillment(expireDate);
            }
        }
        return branchBdeFulfillments;
    }

    BranchManagerFulfillment getBranchManagerFulfillmentForABranchManager(String currentBranchManagerId) {
        for (BranchManagerFulfillment branchManagerFulfillment : branchManagerFulfillments) {
            if ((branchManagerFulfillment.getBranchManager().getEmployeeId()).equals(currentBranchManagerId)) {
                return branchManagerFulfillment;

            }
        }
        return null;
    }

    BranchBdeFulfillment getBranchBDEFulfillmentForABranchBDE(String currentBranchBdeId) {
        for (BranchBdeFulfillment branchBdeFulfillment : branchBDEFulfillments) {
            if (branchBdeFulfillment != null) {
                if ((branchBdeFulfillment.getBranchBde().getEmployeeId()).equals(currentBranchBdeId)) {
                    return branchBdeFulfillment;

                }
            }
        }
        return null;
    }

    public Set<BranchManagerFulfillment> addBranchManagerFulfillment(String currentBranchManager, BranchManagerFulfillment branchManagerFulfillment) {
        this.branchManagerFulfillments.add(branchManagerFulfillment);
        this.currentBranchManager = currentBranchManager;
        return branchManagerFulfillments;
    }


    Set<BranchBdeFulfillment> addBranchBDEFulfillment(String branchBde, BranchBdeFulfillment branchBdeFulfillment) {
        this.branchBDEFulfillments.add(branchBdeFulfillment);
        this.currentBranchBdE = branchBde;
        return branchBDEFulfillments;
    }
}
