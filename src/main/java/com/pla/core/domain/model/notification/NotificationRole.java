package com.pla.core.domain.model.notification;

import com.pla.sharedkernel.domain.model.ProcessType;
import com.pla.sharedkernel.identifier.LineOfBusinessEnum;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.nthdimenzion.common.crud.ICrudEntity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Created by pradyumna on 18-06-2015.
 */
@Entity
@Table(name = "notification_role")
@Getter(AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@ToString
public class NotificationRole implements ICrudEntity,Serializable {

    @EmbeddedId
    private RoleId roleId;

    NotificationRole(RoleId roleId){
        this.roleId  = roleId;
    }

    public static NotificationRole createNotificationRoleMapping(String roleType, LineOfBusinessEnum lineOfBusinessEnum, ProcessType processType) {
        RoleId roleId = RoleId.createRoleMapping(roleType, lineOfBusinessEnum, processType);
        return new NotificationRole(roleId);
    }


}
