package com.pla.individuallife.proposal.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

/**
 * Created by Prasant on 26-May-15.
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmploymentDto {

    private String occupation;
    private String employer;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime employmentDate;

    /*private LocalDate employmentDate;// "11/07/2008",*/
    private String employmentType;
    private String address1;
    private String address2;
    private String postalCode;
    private String province;
    private String town;
    private String workPhone;
}
