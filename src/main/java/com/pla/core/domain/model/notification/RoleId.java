package com.pla.core.domain.model.notification;

import com.pla.sharedkernel.domain.model.ProcessType;
import com.pla.sharedkernel.identifier.LineOfBusinessEnum;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * Created by Admin on 6/19/2015.
 */

@Embeddable
@Getter
@Setter(AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@EqualsAndHashCode(of = {"roleType","lineOfBusiness","process"})
public class RoleId implements Serializable {

    @Column(name = "roleType")
    private String roleType;

    @Column(name = "lineOfBusiness")
    private String lineOfBusiness;

    @Column(name = "process")
    private String process;

    RoleId(String roleType,LineOfBusinessEnum lineOfBusinessEnum,ProcessType process){
        this.roleType  = roleType;
        this.lineOfBusiness = lineOfBusinessEnum.name();
        this.process = process.name();
    }

    public static RoleId createRoleMapping(String roleType, LineOfBusinessEnum lineOfBusiness ,ProcessType process){
        return new RoleId(roleType,lineOfBusiness,process);
    }

}
