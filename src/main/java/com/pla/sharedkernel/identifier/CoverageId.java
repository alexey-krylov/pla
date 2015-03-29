package com.pla.sharedkernel.identifier;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * @author: pradyumna
 * @since 1.0 13/03/2015
 */
@AllArgsConstructor
@EqualsAndHashCode(of = "coverageId")
@Embeddable
@NoArgsConstructor
public class CoverageId implements Serializable {

    private String coverageId;

    public String toString() {
        return coverageId;
    }

}
