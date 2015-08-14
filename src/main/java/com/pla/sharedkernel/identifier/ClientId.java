package com.pla.sharedkernel.identifier;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * Created by Admin on 5/28/2015.
 */
@AllArgsConstructor
@EqualsAndHashCode(of = "clientId")
@Embeddable
@NoArgsConstructor
@Getter
@JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
public class ClientId implements Serializable {

    private String clientId;

    public String toString() {
        return clientId;
    }

}
