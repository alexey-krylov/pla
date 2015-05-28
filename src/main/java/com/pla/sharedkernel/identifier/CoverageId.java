package com.pla.sharedkernel.identifier;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * @author: pradyumna
 * @since 1.0 13/03/2015
 */
@EqualsAndHashCode(of = "coverageId")
@Embeddable
@NoArgsConstructor
@Getter
@Setter
@JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
public class CoverageId implements Serializable {

    private String coverageId;

    public CoverageId(String s) {
        this.coverageId = s;
    }

    public String toString() {
        return coverageId;
    }

}
