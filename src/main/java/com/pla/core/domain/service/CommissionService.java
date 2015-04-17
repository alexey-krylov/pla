package com.pla.core.domain.service;

import com.pla.core.domain.model.Admin;
import com.pla.core.domain.model.plan.Plan;
import com.pla.core.domain.model.plan.commission.Commission;
import com.pla.core.domain.model.plan.commission.CommissionTerm;
import com.pla.core.dto.CommissionTermDto;
import com.pla.core.repository.PlanRepository;
import com.pla.sharedkernel.domain.model.CommissionDesignation;
import com.pla.sharedkernel.domain.model.CommissionTermType;
import com.pla.sharedkernel.domain.model.CommissionType;
import com.pla.sharedkernel.domain.model.PremiumFee;
import com.pla.sharedkernel.identifier.CommissionId;
import com.pla.sharedkernel.identifier.PlanId;
import org.joda.time.LocalDate;
import org.nthdimenzion.common.service.JpaRepositoryFactory;
import org.nthdimenzion.ddd.domain.annotations.DomainService;
import org.nthdimenzion.object.utils.IIdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by User on 3/31/2015.
 */
@DomainService
public class CommissionService {


    private static final Logger LOGGER = LoggerFactory.getLogger(Commission.class);

    private AdminRoleAdapter adminRoleAdapter;

    private IIdGenerator iIdGenerator;

    private JpaRepositoryFactory jpaRepositoryFactory;

    private PlanRepository planRepository;


    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    CommissionService(JpaRepositoryFactory jpaRepositoryFactory, AdminRoleAdapter adminRoleAdapter, IIdGenerator iIdGenerator, PlanRepository planRepository) {
        this.jpaRepositoryFactory = jpaRepositoryFactory;
        this.adminRoleAdapter = adminRoleAdapter;
        this.iIdGenerator = iIdGenerator;
        this.planRepository = planRepository;
    }

    public Commission createCommission(String planId, CommissionDesignation availableFor, CommissionType commissionType, PremiumFee premiumFee, LocalDate fromDate, Set<CommissionTermDto> commissionTermsDto, UserDetails userDetails) {
        Admin admin = adminRoleAdapter.userToAdmin(userDetails);
        PlanId planid = new PlanId(planId);
        TypedQuery typedQuery = entityManager.createNamedQuery("findAllCommissionByPlanIdAndDesignationId", Commission.class).setParameter("planId", planid).
                setParameter("availableFor", availableFor);
        List resultSet = typedQuery.getResultList();

        if (isNotEmpty(resultSet)) {
            JpaRepository<Commission, CommissionId> commissionRepository = jpaRepositoryFactory.getCrudRepository(Commission.class);
            Commission existingCommission = entityManager.createNamedQuery("findAllCommissionByPlanIdAndDesignationId", Commission.class).setParameter("planId", planid).
                    setParameter("availableFor", availableFor).getResultList().get(0);
            existingCommission.validateNewCommissionPeriodForAPlanAndDesignation(fromDate);
            try {
                commissionRepository.save(existingCommission.expireCommission(fromDate.minusDays(1)));
            } catch (RuntimeException e) {
                LOGGER.error("Error in creating commission", e);
            }
        }
        Plan plan = planRepository.findOne(new PlanId(planId));
        CommissionId commissionId = new CommissionId(iIdGenerator.nextId());
        Commission commission = admin.createCommission(commissionId, planid, availableFor, commissionType, premiumFee, fromDate);
        Set<CommissionTerm> commissionTerms = commissionTermsDto.stream().map(new CommissionTermTransformer()).collect(Collectors.toSet());
        return commission.addCommissionTerm(commissionTerms, plan);
    }

    public Commission updateCommissionTerm(String planId, Commission commission, Set<CommissionTermDto> commissionTermsDto, UserDetails userDetails) {
        Admin admin = adminRoleAdapter.userToAdmin(userDetails);
        Plan plan = planRepository.findOne(new PlanId(planId));
        Set<CommissionTerm> commissionTerms = commissionTermsDto.stream().map(new CommissionTermTransformer()).collect(Collectors.toSet());
        return admin.updateCommissionTerm(commission, commissionTerms, plan);

    }

    private class CommissionTermTransformer implements Function<CommissionTermDto, CommissionTerm> {

        @Override
        public CommissionTerm apply(CommissionTermDto commissionTermDto) {
            CommissionTermDto updatedCommissionTermDto = updateEndYearForSingleTermType(commissionTermDto);
            return CommissionTerm.createCommissionTerm(updatedCommissionTermDto.getStartYear(), updatedCommissionTermDto.getEndYear(), updatedCommissionTermDto.getCommissionPercentage(), updatedCommissionTermDto.getCommissionTermType());
        }

        CommissionTermDto updateEndYearForSingleTermType(CommissionTermDto commissionTermDto) {
            if (commissionTermDto.getCommissionTermType().equals(CommissionTermType.SINGLE))
                commissionTermDto.setEndYear(commissionTermDto.getStartYear());
            return commissionTermDto;
        }
    }
}
