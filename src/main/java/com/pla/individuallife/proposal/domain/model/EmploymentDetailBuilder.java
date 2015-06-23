package com.pla.individuallife.proposal.domain.model;

import org.joda.time.DateTime;

public class EmploymentDetailBuilder {
    private String occupationId;
    private String occupationClass;
    private String employer;
    private DateTime employmentDate;
    private String employmentTypeId;
    private long workPhone;
    private Address address;

    public EmploymentDetailBuilder withOccupationId(String occupationId) {
        this.occupationId = occupationId;
        return this;
    }

    public EmploymentDetailBuilder withOccupationClass(String occupationClass) {
        this.occupationClass = occupationClass;
        return this;
    }

    public EmploymentDetailBuilder withEmployer(String employer) {
        this.employer = employer;
        return this;
    }

    public EmploymentDetailBuilder withEmploymentDate(DateTime employmentDate) {
        this.employmentDate = employmentDate;
        return this;
    }

    public EmploymentDetailBuilder withEmploymentTypeId(String employmentTypeId) {
        this.employmentTypeId = employmentTypeId;
        return this;
    }

    public EmploymentDetailBuilder withWorkPhone(long workPhone) {
        this.workPhone = workPhone;
        return this;
    }

    public EmploymentDetailBuilder withAddress(Address address) {
        this.address = address;
        return this;
    }

    public EmploymentDetail createEmploymentDetail() {
        return new EmploymentDetail(occupationId, occupationClass, employer, employmentDate, employmentTypeId, workPhone, address);
    }
}