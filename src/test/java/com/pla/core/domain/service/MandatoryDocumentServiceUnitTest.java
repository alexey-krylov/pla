package com.pla.core.domain.service;

import com.pla.core.domain.exception.MandatoryDocumentException;
import com.pla.core.domain.model.Admin;
import com.pla.core.domain.model.MandatoryDocument;
import com.pla.core.domain.model.ProcessType;
import com.pla.core.query.CoverageFinder;
import com.pla.core.query.MandatoryDocumentFinder;
import com.pla.core.query.PlanFinder;
import com.pla.core.specification.MandatoryDocumentIsAssociatedWithPlan;
import com.pla.sharedkernel.identifier.CoverageId;
import com.pla.sharedkernel.identifier.PlanId;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.nthdimenzion.security.service.UserLoginDetailDto;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.invokeGetterMethod;

/**
 * Created by Admin on 3/30/2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class MandatoryDocumentServiceUnitTest {

    @Mock
    private AdminRoleAdapter adminRoleAdapter;

    @Mock
    private MandatoryDocumentFinder mandatoryDocumentFinder;

    @Mock
    private PlanFinder planFinder;

    @Mock
    private CoverageFinder coverageFinder;

    @Mock
    MandatoryDocumentIsAssociatedWithPlan mandatoryDocumentIsAssociatedWithPlan;

    MandatoryDocumentService mandatoryDocumentService;

    private UserDetails userDetails;

    private Admin admin;

    @Before
    public void setUp() {
        mandatoryDocumentService = new MandatoryDocumentService(adminRoleAdapter,mandatoryDocumentFinder,planFinder,coverageFinder,mandatoryDocumentIsAssociatedWithPlan);
        userDetails = UserLoginDetailDto.createUserLoginDetailDto("", "");
        admin = new Admin();
    }

    @Test
    public void givenProductAndOptionalCoverageWithClaimProcess_whenUserHasAdminRole_thenItShouldCreateTheMandatoryDocumentForTheGivenProduct() {
        MandatoryDocument mandatoryDocument = getMandatoryDocument();
        assertNotNull(mandatoryDocument);
        assertEquals(new PlanId("P001"), invokeGetterMethod(mandatoryDocument, "getPlanId"));
        assertEquals(new CoverageId("C001"), invokeGetterMethod(mandatoryDocument, "getCoverageId"));
        assertEquals(ProcessType.CLAIM, invokeGetterMethod(mandatoryDocument, "getProcess"));
        assertEquals(2, mandatoryDocument.getDocuments().size());
    }

    @Test(expected = MandatoryDocumentException.class)
    public void givenProductAndOptionalCoverageWithClaimProcess_whenUserHasAdminRoleDocumentIsAssociatedWithPlan_thenItShouldThrowAnException() {
        Set<String> documents = new HashSet<>();
        documents.add("DOCUMENT_ONE");
        documents.add("DOCUMENT_TWO");
        when(adminRoleAdapter.userToAdmin(userDetails)).thenReturn(admin);
        when(mandatoryDocumentIsAssociatedWithPlan.isSatisfiedBy(anyObject())).thenReturn(false);
        MandatoryDocument mandatoryDocument =  mandatoryDocumentService.createMandatoryDocument("P001", "C001", ProcessType.CLAIM, documents, userDetails);
        assertNull(mandatoryDocument);

    }


    @Test
    public void givenIdAndSetOfDocuments_whenUserHasAdminRole_thenItShouldUpdateTheMandatoryDocumentForTheGivenProduct() {
        MandatoryDocument mandatoryDocument = getMandatoryDocument();
        assertNotNull(mandatoryDocument);
        Set<String> documents = new HashSet<>();
        documents.add("DOCUMENT_THREE");
        documents.add("DOCUMENT_FOUR");
        documents.add("DOCUMENT_FIVE");
        MandatoryDocument updatedMandatoryDocument = mandatoryDocumentService.updateMandatoryDocument(mandatoryDocument, documents, userDetails);
        assertEquals(3, updatedMandatoryDocument.getDocuments().size());

    }

    private MandatoryDocument getMandatoryDocument(){
        Set<String> documents = new HashSet<>();
        documents.add("DOCUMENT_ONE");
        documents.add("DOCUMENT_TWO");
        when(adminRoleAdapter.userToAdmin(userDetails)).thenReturn(admin);
        when(mandatoryDocumentIsAssociatedWithPlan.isSatisfiedBy(anyObject())).thenReturn(true);
        return mandatoryDocumentService.createMandatoryDocument("P001","C001",ProcessType.CLAIM,documents,userDetails);
    }

}
