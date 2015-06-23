package com.pla.individuallife.proposal.presentation.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
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
public class EmploymentDto {

    private String occupation;
    private String employer;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime employmentDate;

    /*private LocalDate employmentDate;// "11/07/2008",*/
    private String employmentType;
    private String address1;
    private String address2;
    private int postalCode;
    private String province;
    private String town;
    private long workPhone;
}
