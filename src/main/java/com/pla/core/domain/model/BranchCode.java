package com.pla.core.domain.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.lang.invoke.SerializedLambda;

/**
 * Created by User on 3/24/2015.
 */
@Embeddable
@NoArgsConstructor
@Getter
@EqualsAndHashCode(of = "branchCode")
public class BranchCode implements Serializable{

    private String branchCode;

    public BranchCode(String branchCode){
        this.branchCode = branchCode;
    }
}
