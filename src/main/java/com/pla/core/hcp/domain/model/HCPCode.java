package com.pla.core.hcp.domain.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;
import org.nthdimenzion.common.crud.ICrudEntity;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;
import org.springframework.util.Assert;

import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * Created by Mohan Sharma on 12/17/2015.
 */
@ValueObject
@Immutable
@Embeddable
@EqualsAndHashCode(of = "hcpCode")
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class HCPCode implements Serializable{

    private String hcpCode;

    public static String getHCPCode(String townName, String runningSequence, String hcpCategoryCode){
        Assert.notNull(townName, "Town Name is empty");
        Assert.notNull(runningSequence, "Sequence Number For HCPCode is empty");
        Assert.notNull(hcpCategoryCode, "HCPCategoryCode is empty");
        StringBuffer stringBuffer = new StringBuffer(townName.trim().toUpperCase());
        stringBuffer.append(runningSequence.trim());
        stringBuffer.append(hcpCategoryCode.trim().toUpperCase());
        return stringBuffer.toString();
    }
}
