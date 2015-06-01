package com.pla.individuallife.quotation.domain.model;

import com.pla.sharedkernel.domain.model.Gender;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;
import org.joda.time.Years;
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

    private String assuredTitle;

    private String assuredFName;

    private String assuredSurname;

    private String assuredNRC;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate assuredDateOfBirth;

    @Enumerated(EnumType.STRING)
    private Gender assuredGender;

    private String assuredMobileNumber;

    private String assuredEmailId;

    private String occupation;


    ProposedAssured(ProposedAssuredBuilder proposedAssuredBuilder) {
        checkArgument(proposedAssuredBuilder != null);
        this.assuredTitle = proposedAssuredBuilder.getTitle();
        this.assuredFName = proposedAssuredBuilder.getFirstName();
        this.assuredSurname = proposedAssuredBuilder.getSurname();
        this.assuredNRC = proposedAssuredBuilder.getNrcNumber();
        this.assuredDateOfBirth = proposedAssuredBuilder.getDateOfBirth();
        this.assuredGender = proposedAssuredBuilder.getGender();
        this.assuredMobileNumber = proposedAssuredBuilder.getMobileNumber();
        this.assuredEmailId = proposedAssuredBuilder.getEmailAddress();
        this.occupation = proposedAssuredBuilder.getOccupation();
    }

    public static ProposedAssuredBuilder proposedAssuredBuilder() {
        return new ProposedAssuredBuilder();
    }


    public Number getAgeNextBirthDay() {
        return Years.yearsBetween(assuredDateOfBirth, LocalDate.now()).getYears() + 1;
    }
}
