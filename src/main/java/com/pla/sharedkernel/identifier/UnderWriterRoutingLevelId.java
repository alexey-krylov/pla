package com.pla.sharedkernel.identifier;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * Created by Admin on 5/13/2015.
 */
@AllArgsConstructor
@EqualsAndHashCode(of = "underWriterRoutingLevelId")
@Embeddable
@NoArgsConstructor
@Getter
@JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
public class UnderWriterRoutingLevelId  implements Serializable {

    private String underWriterRoutingLevelId;

    public String toString() {
        return underWriterRoutingLevelId;
    }

}
