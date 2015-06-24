package com.pla.individuallife.proposal.domain.model;

import lombok.AccessLevel;
import lombok.Getter;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

/**
 * Created by pradyumna on 22-05-2015.
 */

@Getter(value = AccessLevel.PACKAGE)
public class EmploymentDetail {
    private String occupationId;
    private String occupationClass;
    private String employer;
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime employmentDate;
    private String employmentTypeId;
    private long workPhone;
    private Address address;

    EmploymentDetail(String occupationId, String occupationClass, String employer, DateTime employmentDate, String employmentTypeId, long workPhone, Address address) {
        this.occupationId = occupationId;
        this.occupationClass = occupationClass;
        this.employer = employer;
        this.employmentDate = employmentDate;
        this.employmentTypeId = employmentTypeId;
        this.workPhone = workPhone;
        this.address = address;
    }
}
