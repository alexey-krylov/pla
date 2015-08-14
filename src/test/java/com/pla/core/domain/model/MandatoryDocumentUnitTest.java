package com.pla.core.domain.model;

import com.google.common.collect.Sets;
import com.pla.sharedkernel.domain.model.ProcessType;
import com.pla.sharedkernel.identifier.CoverageId;
import com.pla.sharedkernel.identifier.PlanId;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.util.ReflectionTestUtils.invokeGetterMethod;

/**
 * Created by Admin on 3/30/2015.
 */
public class MandatoryDocumentUnitTest {

    @Test
    public void givenThePlanIdCoverageId_thenItShouldCreateMandatoryDocument(){
        Set<String> documents = Sets.newHashSet();
        documents.add("DOCUMENT_ONE");
        documents.add("DOCUMENT_TWO");
        MandatoryDocument mandatoryDocument = MandatoryDocument.createMandatoryDocumentWithCoverageId(new PlanId("P001"), new CoverageId("C001"), ProcessType.ENDORSEMENT, documents);

        assertNotNull(mandatoryDocument);
        assertEquals(new PlanId("P001"), invokeGetterMethod(mandatoryDocument, "getPlanId"));
        assertEquals(new CoverageId("C001"), invokeGetterMethod(mandatoryDocument, "getCoverageId"));
        assertEquals(ProcessType.ENDORSEMENT, invokeGetterMethod(mandatoryDocument, "getProcess"));
        assertEquals(2, mandatoryDocument.getDocuments().size());
    }

    @Test
    public void givenTheDocuments_ThenItShouldUpdateMandatoryDocumentWithTheGivenDocuments(){
        Set<String> documents = Sets.newHashSet();
        documents.add("DOCUMENT_ONE");
        documents.add("DOCUMENT_TWO");
        MandatoryDocument mandatoryDocument = MandatoryDocument.createMandatoryDocumentWithCoverageId(new PlanId("P001"), new CoverageId("C001"), ProcessType.ENDORSEMENT, documents);
        assertNotNull(mandatoryDocument);
        documents = Sets.newHashSet();
        documents.add("DOCUMENT_THREE");
        documents.add("DOCUMENT_FOUR");
        documents.add("DOCUMENT_FIVE");
        MandatoryDocument updatedMandatoryDocument =   mandatoryDocument.updateMandatoryDocument(documents);
        assertEquals(3, updatedMandatoryDocument.getDocuments().size());
    }

    @Test(expected = NullPointerException.class)
    public void givenNoDocumentsToUpdate_thenItShouldThrowAnException(){
        Set<String> documents = Sets.newHashSet();
        documents.add("DOCUMENT_ONE");
        documents.add("DOCUMENT_TWO");
        MandatoryDocument mandatoryDocument = MandatoryDocument.createMandatoryDocumentWithCoverageId(new PlanId("P001"), new CoverageId("C001"), ProcessType.ENDORSEMENT, documents);
        assertNotNull(mandatoryDocument);

        MandatoryDocument updatedMandatoryDocument = mandatoryDocument.updateMandatoryDocument(null);
    }

}
