package com.pla.sharedkernel.domain.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Preconditions;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.util.regex.Pattern;

/**
 * Created by pradyumna on 22-05-2015.
 */
@EqualsAndHashCode(of = "proposalNumber")
@Embeddable
@NoArgsConstructor
@Getter
@JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
public class ProposalNumber {
    private String proposalNumber;

    ProposalNumber(String proposalNumber) {
        Preconditions.checkArgument(proposalNumber != null);
        this.proposalNumber = proposalNumber;
    }

    public static void main(String[] args) {
        String regex = "/\\b[6][-][2]\\b/g";
        System.out.println(Pattern.compile(regex).matcher("6-2").matches());
    }

    @Override
    public String toString() {
        return proposalNumber;
    }
}
