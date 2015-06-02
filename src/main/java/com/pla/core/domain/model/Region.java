package com.pla.core.domain.model;

import com.google.common.collect.Sets;
import com.pla.core.domain.exception.RegionDomainException;
import lombok.*;
import org.hibernate.annotations.Cascade;
import org.joda.time.LocalDate;
import org.nthdimenzion.common.crud.ICrudEntity;

import javax.persistence.*;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by User on 3/20/2015.
 */
@Entity
@Table(name = "region", uniqueConstraints = {@UniqueConstraint(name = "UNQ_REGION_CODE_NAME", columnNames = {"regionCode", "regionName"})})
@EqualsAndHashCode(of = {"regionName", "regionCode"})
@ToString(of = {"regionCode", "regionName"})
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Getter(value = AccessLevel.PACKAGE)
public class Region implements ICrudEntity {
    @Id
    private String regionCode;

    private String regionName;

    private String regionalManager;

    @OneToMany(targetEntity = Branch.class, fetch = FetchType.EAGER)
    @JoinTable(name = "region_branch", joinColumns = @JoinColumn(name = "REGION_CODE"), inverseJoinColumns = @JoinColumn(name = "BRANCH_CODE"))
    private Set<Branch> branches;

    @ElementCollection(targetClass = RegionalManagerFulfillment.class, fetch = FetchType.EAGER)
    @OrderColumn
    @JoinTable(name = "REGION_MANAGER_FUlFILLMENT", joinColumns = @JoinColumn(name = "REGION_CODE"))
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private Set<RegionalManagerFulfillment> regionalManagerFulfillments = Sets.newHashSet();


    Region(String regionCode, String regionName, String regionalManager) {
        checkArgument(isNotEmpty(regionCode));
        checkArgument(isNotEmpty(regionalManager));
        this.regionCode = regionCode;
        this.regionName = regionName;
        this.regionalManager = regionalManager;
    }


    public Region assignRegionalManager(String employeeId, String firstName, String lastName, LocalDate effectiveFrom) {
        RegionalManager regionalManager = new RegionalManager(employeeId, firstName, lastName);
        RegionalManagerFulfillment regionalManagerFulfillment = new RegionalManagerFulfillment(regionalManager, effectiveFrom);
        RegionalManagerFulfillment currentRegionFulfillment = getRegionalManagerFulfillmentForARegionalManager(this.regionalManager);
        if (currentRegionFulfillment != null) {
            boolean isRegionalManagerExists = isNewRegionalManagerFulfillmentValid(effectiveFrom, currentRegionFulfillment.getFromDate());
            if (isRegionalManagerExists) {
                throw new RegionDomainException("Another Regional Manager has already been associated with this region for this period");
            }
            this.regionalManagerFulfillments = updateRegionalMangerFulfillment(this.regionalManagerFulfillments, currentRegionFulfillment.getRegionalManager(), effectiveFrom.plusDays(-1));
        }
        this.regionalManagerFulfillments = addRegionalMangerFulfillment(employeeId, regionalManagerFulfillment);
        return this;
    }

    public boolean isNewRegionalManagerFulfillmentValid(LocalDate newRegionalManagerFromDate, LocalDate currentRegionalManagerFromDate) {
        return newRegionalManagerFromDate.isAfter(currentRegionalManagerFromDate);
    }

    public Set<RegionalManagerFulfillment> updateRegionalMangerFulfillment(Set<RegionalManagerFulfillment> regionalManagerFulfillments, RegionalManager regionalManagerToBeExpired,
                                                                           LocalDate expireDate) {
        for (RegionalManagerFulfillment regionalManagerFulfillment : regionalManagerFulfillments) {
            if (regionalManagerFulfillment.getRegionalManager().equals(regionalManagerToBeExpired) && regionalManagerFulfillment.getThruDate() == null) {
                regionalManagerFulfillment.expireFulfillment(expireDate);
            }
        }
        return regionalManagerFulfillments;
    }

    public RegionalManagerFulfillment getRegionalManagerFulfillmentForARegionalManager(String currentRegionalManagerId) {
        for (RegionalManagerFulfillment regionalManagerFulfillment : regionalManagerFulfillments) {
            if (regionalManagerFulfillment != null && (regionalManagerFulfillment.getRegionalManager().getEmployeeId()).equals(currentRegionalManagerId)) {
                return regionalManagerFulfillment;

            }
        }
        return null;
    }

    public Set<RegionalManagerFulfillment> addRegionalMangerFulfillment(String regionalManager, RegionalManagerFulfillment regionalManagerFulfillment) {
        this.regionalManagerFulfillments.add(regionalManagerFulfillment);
        this.regionalManager = regionalManager;
        return regionalManagerFulfillments;
    }
}
