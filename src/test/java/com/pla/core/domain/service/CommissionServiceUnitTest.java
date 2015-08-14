package com.pla.core.domain.service;

import com.google.common.collect.Sets;
import com.pla.core.domain.model.Admin;
import com.pla.core.domain.model.plan.Plan;
import com.pla.core.domain.model.plan.commission.Commission;
import com.pla.core.domain.model.plan.commission.CommissionTerm;
import com.pla.core.dto.CommissionTermDto;
import com.pla.core.repository.PlanRepository;
import com.pla.sharedkernel.domain.model.CommissionTermType;
import com.pla.sharedkernel.identifier.PlanId;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.nthdimenzion.common.service.JpaRepositoryFactory;
import org.nthdimenzion.object.utils.IIdGenerator;
import org.nthdimenzion.security.service.UserLoginDetailDto;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.invokeGetterMethod;

/**
 * Created by User on 4/10/2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class CommissionServiceUnitTest {
    @Mock
    private AdminRoleAdapter adminRoleAdapter;

    @Mock
    private JpaRepositoryFactory jpaRepositoryFactory;

    @Mock
    private IIdGenerator idGenerator;

    @Mock
    private EntityManager entityManager;

    private CommissionService commissionService;

    private UserDetails userDetails;

    private Admin admin;

    @Mock
    PlanRepository planRepository;

    @Mock
    Plan plan;

    @Before
    public void setUp() {
        userDetails = UserLoginDetailDto.createUserLoginDetailDto("", "");
        commissionService = new CommissionService(jpaRepositoryFactory, adminRoleAdapter, idGenerator, planRepository);
        admin = new Admin();
    }
    @Test
    public void givenAUpdateCommissionCommandShouldUpdateCommission() {
        String commissionId = "1234";
        String planId = "123143sfd";
        Set<Integer> policyTerms = Sets.newHashSet();
        policyTerms.add(new Integer(1));
        policyTerms.add(new Integer(2));
        policyTerms.add(new Integer(3));
        policyTerms.add(new Integer(4));

        Set<CommissionTermDto> commissionTermsDtos = Sets.newHashSet();
        CommissionTermDto commissionTermDto = new CommissionTermDto();
        commissionTermDto.setStartYear(1);
        commissionTermDto.setEndYear(3);
        commissionTermDto.setCommissionTermType(CommissionTermType.RANGE);
        commissionTermDto.setCommissionPercentage(new BigDecimal(34.90));
        commissionTermsDtos.add(commissionTermDto);

        when(idGenerator.nextId()).thenReturn(commissionId);
        Commission commission = new Commission();
        when(adminRoleAdapter.userToAdmin(userDetails)).thenReturn(admin);
        when(plan.getAllowedPolicyTerm()).thenReturn(policyTerms);
        when(planRepository.findOne(new PlanId(planId))).thenReturn(plan);
        Commission updatedCommission = commissionService.updateCommissionTerm(planId ,commission, commissionTermsDtos, userDetails);
        Set<CommissionTerm> updatedCommissionTerms = (Set<CommissionTerm>) invokeGetterMethod(updatedCommission, "getCommissionTerms");

        CommissionTerm commissionTerm = CommissionTerm.createCommissionTerm(1, 3, new BigDecimal(34.90), CommissionTermType.RANGE);

        assertThat(updatedCommissionTerms, CoreMatchers.hasItems(commissionTerm));

    }

    @Test
    public void givenAUpdateCommissionCommandShouldUpdateCommissionForSingleTermType() {
        String commissionId = "1234";
        String planId = "123143sfd";
        Set<Integer> policyTerms = Sets.newHashSet();
        policyTerms.add(new Integer(1));
        policyTerms.add(new Integer(2));
        policyTerms.add(new Integer(3));
        policyTerms.add(new Integer(4));

        Set<CommissionTermDto> commissionTermsDtos = Sets.newHashSet();
        CommissionTermDto commissionTermDto = new CommissionTermDto();
        commissionTermDto.setStartYear(1);
        commissionTermDto.setCommissionTermType(CommissionTermType.SINGLE);
        commissionTermDto.setCommissionPercentage(new BigDecimal(34.90));
        commissionTermsDtos.add(commissionTermDto);

        when(idGenerator.nextId()).thenReturn(commissionId);
        Commission commission = new Commission();
        when(adminRoleAdapter.userToAdmin(userDetails)).thenReturn(admin);
        when(plan.getAllowedPolicyTerm()).thenReturn(policyTerms);
        when(planRepository.findOne(new PlanId(planId))).thenReturn(plan);
        Commission updatedCommission = commissionService.updateCommissionTerm(planId ,commission, commissionTermsDtos, userDetails);
        Set<CommissionTerm> updatedCommissionTerms = (Set<CommissionTerm>) invokeGetterMethod(updatedCommission, "getCommissionTerms");

        CommissionTerm commissionTerm = CommissionTerm.createCommissionTerm(1, 1, new BigDecimal(34.90), CommissionTermType.SINGLE);

        assertThat(updatedCommissionTerms, CoreMatchers.hasItems(commissionTerm));

    }
    @Test
    public void givenAUpdateCommissionCommandShouldUpdateCommissionForCombinationOfSingleAndRangeTermType() {
        String commissionId = "1234";
        String planId = "123143sfd";
        Set<Integer> policyTerms = Sets.newHashSet();
        policyTerms.add(new Integer(1));
        policyTerms.add(new Integer(2));
        policyTerms.add(new Integer(3));
        policyTerms.add(new Integer(4));
        policyTerms.add(new Integer(6));
        policyTerms.add(new Integer(7));
        policyTerms.add(new Integer(8));

        Set<CommissionTermDto> commissionTermsDtos = Sets.newHashSet();
        CommissionTermDto commissionTermDtoSingle = new CommissionTermDto();
        commissionTermDtoSingle.setStartYear(1);
        commissionTermDtoSingle.setCommissionTermType(CommissionTermType.SINGLE);
        commissionTermDtoSingle.setCommissionPercentage(new BigDecimal(34.90));
        commissionTermsDtos.add(commissionTermDtoSingle);
        CommissionTermDto commissionTermDtoSingleSecond = new CommissionTermDto();
        commissionTermDtoSingleSecond.setStartYear(8);
        commissionTermDtoSingleSecond.setCommissionTermType(CommissionTermType.SINGLE);
        commissionTermDtoSingleSecond.setCommissionPercentage(new BigDecimal(34.90));
        commissionTermsDtos.add(commissionTermDtoSingleSecond);
        CommissionTermDto commissionTermDtoRange = new CommissionTermDto();
        commissionTermDtoRange.setStartYear(2);
        commissionTermDtoRange.setEndYear(5);
        commissionTermDtoRange.setCommissionTermType(CommissionTermType.RANGE);
        commissionTermDtoRange.setCommissionPercentage(new BigDecimal(34.90));
        commissionTermsDtos.add(commissionTermDtoRange);
        CommissionTermDto commissionTermDtoRangeSecond = new CommissionTermDto();
        commissionTermDtoRangeSecond.setStartYear(6);
        commissionTermDtoRangeSecond.setEndYear(7);
        commissionTermDtoRangeSecond.setCommissionTermType(CommissionTermType.RANGE);
        commissionTermDtoRangeSecond.setCommissionPercentage(new BigDecimal(34.90));
        commissionTermsDtos.add(commissionTermDtoRangeSecond);

        when(idGenerator.nextId()).thenReturn(commissionId);
        Commission commission = new Commission();
        when(adminRoleAdapter.userToAdmin(userDetails)).thenReturn(admin);
        when(plan.getAllowedPolicyTerm()).thenReturn(policyTerms);
        when(planRepository.findOne(new PlanId(planId))).thenReturn(plan);
        Commission updatedCommission = commissionService.updateCommissionTerm(planId ,commission, commissionTermsDtos, userDetails);
        Set<CommissionTerm> updatedCommissionTerms = (Set<CommissionTerm>) invokeGetterMethod(updatedCommission, "getCommissionTerms");


        assertEquals(updatedCommissionTerms.size(), 4);

    }



}
