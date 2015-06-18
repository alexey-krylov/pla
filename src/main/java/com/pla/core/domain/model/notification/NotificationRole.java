package com.pla.core.domain.model.notification;

import com.pla.sharedkernel.domain.model.ProcessType;
import com.pla.sharedkernel.identifier.LineOfBusinessEnum;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by pradyumna on 18-06-2015.
 */
@Entity
@Getter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class NotificationRole {

    @Id
    @GeneratedValue
    private long id;
    private String roleType;
    private LineOfBusinessEnum lineOfBusiness;
    private ProcessType processType;
}
