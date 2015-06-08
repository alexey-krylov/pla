package com.pla.individuallife.identifier;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.io.Serializable;

/**
 * Created by ASUS on 27-May-15.
 */

@EqualsAndHashCode(of = "proposalId")
@NoArgsConstructor
@Getter
@JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
public class ProposalId implements Serializable {

    private String proposalId;

    public ProposalId(String proposalId) {
        this.proposalId = proposalId;
    }

    public String toString() {
        return this.proposalId;
    }
}
