package com.pla.grouplife.claim.domain.model;

import com.pla.sharedkernel.domain.model.FamilyId;
import lombok.Getter;
import org.joda.time.DateTime;

/**
 * Created by Mirror on 8/21/2015.
 */
@Getter
public class AssuredDetail {

    private FamilyId familyId;

    private String firstName;

    private String lastName;

    private DateTime dateOfBirth;

    public AssuredDetail(FamilyId familyId,String firstName,String lastName,DateTime dateOfBirth) {
        this.familyId=familyId;
        this.firstName=firstName;
        this.lastName=lastName;
        this.dateOfBirth=dateOfBirth;
    }
}
