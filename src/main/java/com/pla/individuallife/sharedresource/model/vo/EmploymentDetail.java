package com.pla.individuallife.sharedresource.model.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

/**
 * Created by pradyumna on 22-05-2015.
 */

@Getter
@Setter
@NoArgsConstructor
public class EmploymentDetail {
    private String occupationId;
    private String occupationClass;
    private String employer;
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime employmentDate;
    private String employmentTypeId;
    private String workPhone;
    private Address address;

   public EmploymentDetail(String occupationId, String occupationClass, String employer, DateTime employmentDate, String employmentTypeId, String workPhone, Address address) {
        this.occupationId = occupationId;
        this.occupationClass = occupationClass;
        this.employer = employer;
        this.employmentDate = employmentDate;
        this.employmentTypeId = employmentTypeId;
        this.workPhone = workPhone;
        this.address = address;
    }
}
