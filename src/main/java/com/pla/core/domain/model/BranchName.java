package com.pla.core.domain.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * Created by User on 3/24/2015.
 */
@Embeddable
@EqualsAndHashCode(of = "branchName")
@NoArgsConstructor
@Getter
public class BranchName implements Serializable {

    private String branchName;

    public BranchName(String branchName) {
        this.branchName = branchName;
    }
}
