package com.pla.individuallife.proposal.domain.model;

import lombok.Getter;
import org.joda.time.LocalDate;

/**
 * Created by pradyumna on 22-05-2015.
 */
@Getter
public class EmploymentDetail {
    private String occupationId;
    private String occupationClass;
    private String employer;
    private LocalDate employmentDate;
    private String employmentTypeId;
    private long workPhone;
    private Address address;

}
