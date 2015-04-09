package com.pla.core.domain.service;

import com.pla.core.domain.model.Admin;
import com.pla.core.domain.model.plan.commission.Commission;
import com.pla.core.domain.model.plan.commission.CommissionTerm;
import com.pla.core.dto.CommissionTermDto;
import com.pla.sharedkernel.domain.model.CommissionDesignation;
import com.pla.sharedkernel.domain.model.CommissionTermType;
import com.pla.sharedkernel.domain.model.CommissionType;
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

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    CommissionService(JpaRepositoryFactory jpaRepositoryFactory, AdminRoleAdapter adminRoleAdapter, IIdGenerator iIdGenerator) {
        this.jpaRepositoryFactory = jpaRepositoryFactory;
        this.adminRoleAdapter = adminRoleAdapter;
        this.iIdGenerator = iIdGenerator;
    }

    public Commission createCommission(String planId, CommissionDesignation availableFor, CommissionType commissionType, CommissionTermType termType, LocalDate fromDate, Set<CommissionTermDto> commissionTermsDto, UserDetails userDetails) {
        Admin admin = adminRoleAdapter.userToAdmin(userDetails);
        PlanId planid = new PlanId(planId);
        List<Commission> commissions = entityManager.createNamedQuery("findAllCommissionByPlanIdAndDesignationId", Commission.class).setParameter("planId", planid).
                setParameter("availableFor", availableFor).getResultList();

        if (isNotEmpty(commissions)) {
            JpaRepository<Commission, CommissionId> commissionRepository = jpaRepositoryFactory.getCrudRepository(Commission.class);
            try {
                commissionRepository.save(commissions.get(0).expireCommission(fromDate.minusDays(1)));
            } catch (RuntimeException e) {
                LOGGER.error("Error in creating commission", e);
            }
        }
        CommissionId commissionId = new CommissionId(iIdGenerator.nextId());
        Commission commission = admin.createCommission(commissionId, planid, availableFor, commissionType, termType, fromDate);
        Set<CommissionTerm> commissionTerms = commissionTermsDto.stream().map(new CommissionTermTransformer()).collect(Collectors.toSet());
        return commission.addCommissionTerm(commissionTerms);
    }

    public Commission updateCommissionTerm(CommissionId commissionId, Set<CommissionTermDto> commissionTermsDto, UserDetails userDetails) {
        Admin admin = adminRoleAdapter.userToAdmin(userDetails);
        JpaRepository<Commission, CommissionId> commissionRepository = jpaRepositoryFactory.getCrudRepository(Commission.class);
        Commission commission = commissionRepository.findOne(commissionId);
        Set<CommissionTerm> commissionTerms = commissionTermsDto.stream().map(new CommissionTermTransformer()).collect(Collectors.toSet());
        return admin.updateCommissionTerm(commission,commissionTerms);

    }

    private class CommissionTermTransformer implements Function<CommissionTermDto, CommissionTerm> {

        @Override
        public CommissionTerm apply(CommissionTermDto commissionTermDto) {
            return CommissionTerm.createCommissionTerm(commissionTermDto.getStartYear(), commissionTermDto.getEndYear(), commissionTermDto.getCommissionPercentage());
        }
    }
}
