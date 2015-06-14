package com.pla.individuallife.quotation.domain.model;

import com.pla.sharedkernel.domain.model.Gender;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

/**
 * Created by Karunakar on 5/18/2015.
 */
@Embeddable
@ValueObject
@Getter
@Setter
public class Proposer {

    private String title;

    private String firstName;

    private String surname;

    private String nrcNumber;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime dateOfBirth;

    @Column(name = "proposerGender")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String mobileNumber;

    private String emailAddress;

    public Proposer() {
    }

    public Proposer(String title, String firstName, String surname, String nrcNumber, DateTime dateOfBirth, Gender gender, String mobileNumber, String emailAddress) {
        this.title = title;
        this.firstName = firstName;
        this.surname = surname;
        this.nrcNumber = nrcNumber;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.mobileNumber = mobileNumber;
        this.emailAddress = emailAddress;
    }

}
