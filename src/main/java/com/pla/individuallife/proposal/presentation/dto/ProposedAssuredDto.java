package com.pla.individuallife.proposal.presentation.dto;

import com.pla.sharedkernel.domain.model.Gender;
import com.pla.sharedkernel.domain.model.MaritalStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

/*import org.hibernate.validator.constraints.NotEmpty;*/
/**
 * Created by Prasant on 26-May-15.
 */
@Getter
@Setter
@NoArgsConstructor
public class ProposedAssuredDto {

    private String title;
    private String firstName;
    private String surname;
    private String otherName;
    private String nrc;
    private boolean isProposer;
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime dateOfBirth;
    private Gender gender;
    private String mobileNumber;
    private String emailAddress;
    private MaritalStatus maritalStatus;
    private ResidentialAddressDto residentialAddress;
    private EmploymentDto employment;
    private SpouseDto spouse;
}
