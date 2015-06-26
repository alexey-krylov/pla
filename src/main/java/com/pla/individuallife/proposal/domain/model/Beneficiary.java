package com.pla.individuallife.proposal.domain.model;

import com.google.common.base.Objects;
import com.pla.sharedkernel.domain.model.Gender;
import com.pla.sharedkernel.domain.model.TitleEnum;
import lombok.Getter;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import java.math.BigDecimal;

/**
 * Created by pradyumna on 22-05-2015.
 */
@Getter
public class Beneficiary {

    private TitleEnum title;
    private String firstName;
    private String surname;
    private String nrc;
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime dateOfBirth;
    private Gender gender;
    private String relationshipId;
    private BigDecimal share;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Beneficiary that = (Beneficiary) o;
        return (
                Objects.equal(firstName, that.firstName) &&
                        Objects.equal(surname, that.surname) &&
                        Objects.equal(dateOfBirth, that.dateOfBirth)
        ) || Objects.equal(nrc, that.nrc);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(firstName, surname, nrc, dateOfBirth);
    }
}
