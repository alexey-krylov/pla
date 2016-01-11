package com.pla.grouphealth.claim.cashless.domain.model;

import lombok.*;
import org.hibernate.annotations.Immutable;
import org.joda.time.DateTime;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import javax.persistence.Embeddable;

/**
 * Created by Mohan Sharma on 1/9/2016.
 */
@ValueObject
@Immutable
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor
@Getter
public class PreAuthorizationRequestDiagnosisTreatmentDetail {
    private String indicateWhether;
    private String pregnancyG;
    private String pregnancyP;
    private String pregnancyL;
    private String pregnancyA;
    private DateTime pregnancyDateOfDelivery;
    private String modeOdDelivery;
    private String nameOfIllnessDisease;
    private String relevantClinicalFinding;
    private String durationOfPresentAilment;
    private DateTime dateOfConsultation;
    private String pastHistoryOfPresentAilment;
    private String provisionalDiagnosis;
    private String lineOfTreatment;
    private String indicateTest;
    private String nameOfSurgery;
    private DateTime dateOfAdmission;
    private int lengthOfStay;
    private String typeOfAccommodation;
}
