package com.pla.core.paypoint.domain.model;

import com.google.common.base.Preconditions;
import lombok.*;
import org.hibernate.annotations.Immutable;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * Created by Rudra on 12/11/2015.
 */
@ValueObject
@Immutable
@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@ToString(of = "payPointId")
@EqualsAndHashCode(of = "payPointId")
public class PayPointId implements Serializable {

    private  String payPointId;

    public PayPointId(String payPointId){
        Preconditions.checkNotNull(payPointId,"PayPointid should not be:%s"+payPointId);
        this.payPointId=payPointId;
    }
}
