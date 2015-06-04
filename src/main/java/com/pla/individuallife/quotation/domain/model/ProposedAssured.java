package com.pla.individuallife.quotation.domain.model;

import com.pla.sharedkernel.domain.model.Gender;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Created by Karunakar on 5/18/2015.
 */
@Embeddable
@Table(name = "assured")
@ValueObject
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
@Setter(value = AccessLevel.PACKAGE)
public class ProposedAssured {

    private String title;

    private String firstName;

    private String surname;

    private String nrcNumber;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String mobileNumber;

    private String emailAddress;

    private String occupation;

    ProposedAssured(ProposedAssuredBuilder proposedAssuredBuilder) {
        checkArgument(proposedAssuredBuilder != null);
        this.title = proposedAssuredBuilder.getTitle();
        this.firstName = proposedAssuredBuilder.getFirstName();
        this.surname = proposedAssuredBuilder.getSurname();
        this.nrcNumber = proposedAssuredBuilder.getNrcNumber();
        this.dateOfBirth = proposedAssuredBuilder.getDateOfBirth();
        this.gender = proposedAssuredBuilder.getGender();
        this.mobileNumber = proposedAssuredBuilder.getMobileNumber();
        this.emailAddress = proposedAssuredBuilder.getEmailAddress();
        this.occupation = proposedAssuredBuilder.getOccupation();
    }

    public static ProposedAssuredBuilder proposedAssuredBuilder() {
        return new ProposedAssuredBuilder();
    }

    /*@JsonIgnore
    public Number getAgeNextBirthDay() {
        return Years.yearsBetween(dateOfBirth, LocalDate.now()).getYears() + 1;
    }*/
}
