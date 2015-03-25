/*
package com.pla.core.domain.service;

import com.pla.core.domain.model.Admin;
import com.pla.core.domain.model.Benefit;
import com.pla.core.domain.model.BenefitId;
import com.pla.core.domain.model.BenefitName;
import com.pla.core.dto.BenefitDto;
import com.pla.core.query.CoverageFinder;
import com.pla.core.specification.BenefitIsAssociatedWithCoverage;
import com.pla.core.specification.CoverageNameIsUnique;
import com.pla.sharedkernel.domain.model.BenefitStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.nthdimenzion.object.utils.IIdGenerator;
import org.nthdimenzion.security.service.UserLoginDetailDto;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.invokeGetterMethod;

*/
/**
 * Created by Admin on 3/25/2015.
 *//*


@RunWith(MockitoJUnitRunner.class)
public class CoverageServiceUnitTest {

    @Mock
    private AdminRoleAdapter adminRoleAdapter;

    @Mock
    private CoverageNameIsUnique coverageNameIsUnique;

    @Mock
    private CoverageFinder coverageFinder;

    @Mock
    private IIdGenerator idGenerator;

    private CoverageService coverageService;

    private UserDetails userDetails;

    private Admin admin;

    @Before
    public void setUp() {
        coverageService = new CoverageService(adminRoleAdapter, coverageNameIsUnique, idGenerator);
        userDetails = UserLoginDetailDto.createUserLoginDetailDto("", "");
        admin = new Admin();

    }


   */
/* @Test
    public void givenACoverageName_whenTheCoverageNameIsUnique_thenItShouldCreateTheCoverageWithActiveState() {
        String benefitId = "BE001";
        String name = "CI Benefit";
        when(idGenerator.nextId()).thenReturn(benefitId);
        when(adminRoleAdapter.userToAdmin(userDetails)).thenReturn(admin);
        Benefit benefit = coverageService.createCoverage(name, userDetails);
        BenefitName createdBenefitName = (BenefitName) invokeGetterMethod(benefit, "getBenefitName");
        assertNotNull(benefit);
        assertEquals(new BenefitId(benefitId), invokeGetterMethod(benefit, "getBenefitId"));
        assertEquals(BenefitStatus.ACTIVE, invokeGetterMethod(benefit, "getStatus"));
        assertEquals(name, createdBenefitName.getBenefitName());
    }*//*


*/
/*
    @Test
    public void givenABenefitWithUpdatedNameItShouldUpdateBenefit() {
        Benefit benefit = getBenefit();
        String name = "CI Benefit";
        when(adminRoleAdapter.userToAdmin(userDetails)).thenReturn(admin);
        when(coverageFinder.getBenefitCountAssociatedWithActiveCoverage("1")).thenReturn(0);
        BenefitDto benefitDto = new BenefitDto(benefit.getBenefitId().getBenefitId(), name);
        when(coverageNameIsUnique.isSatisfiedBy(benefitDto)).thenReturn(true);
        Benefit updatedBenefit = coverageService.updateBenefit(benefit, name, userDetails);
        BenefitName updatedBenefitName = (BenefitName) invokeGetterMethod(updatedBenefit, "getBenefitName");
        assertEquals(BenefitStatus.ACTIVE, invokeGetterMethod(updatedBenefit, "getStatus"));
        assertEquals(name, updatedBenefitName.getBenefitName());
    }
*//*


    @Test
    public void givenABenefitWhenMarkAsUsedItShouldBeInUsedStatus() {
        Benefit benefit = getBenefit();
        Benefit updatedBenefit = null;//coverageService.markBenefitAsUsed(benefit);
        assertEquals(BenefitStatus.INUSE, invokeGetterMethod(updatedBenefit, "getStatus"));
    }


    @Test
    public void givenABenefitItShouldInactivateBenefit() {
        Benefit benefit = getBenefit();
        when(adminRoleAdapter.userToAdmin(userDetails)).thenReturn(admin);
        Benefit updatedBenefit = null;//overageService.inactivateBenefit(benefit, userDetails);
        assertEquals(BenefitStatus.INACTIVE, invokeGetterMethod(updatedBenefit, "getStatus"));
    }

    private Benefit getBenefit() {
        String name = "Accidental death benefit";
        BenefitDto benefitDto = new BenefitDto("1", name);
        boolean isBenefitNameUnique = Boolean.TRUE;
        //when(coverageNameIsUnique.isSatisfiedBy(benefitDto)).thenReturn(isBenefitNameUnique);
        Benefit benefit = null;//admin.createBenefit(isBenefitNameUnique, "1", name);
        return benefit;
    }
}
*/
