package com.pla.individuallife.sharedresource.model.vo;

import com.google.common.base.Objects;
import com.pla.sharedkernel.domain.model.Gender;
import com.pla.sharedkernel.domain.model.TrusteeType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import java.math.BigDecimal;

import static org.nthdimenzion.utils.UtilValidator.isEmpty;

/**
 * Created by pradyumna on 22-05-2015.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Beneficiary {

    private String title;
    private String firstName;
    private String surname;
    private String nrc;
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime dateOfBirth;
    private Gender gender;
    private String relationshipId;
    private BigDecimal share;
    private TrusteeType trusteeType;
    private TrusteeDetail trusteeDetail;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Beneficiary that = (Beneficiary) o;
        return (
                Objects.equal(firstName, that.firstName) &&
                        Objects.equal(surname, that.surname) &&
                        Objects.equal(dateOfBirth, that.dateOfBirth)
        ) || Objects.equal(isEmpty(nrc)?null:nrc, isEmpty(that.nrc)?null:that.nrc);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(firstName, surname, nrc, dateOfBirth);
    }
}
