package com.pla.core.domain.event;

import com.pla.sharedkernel.identifier.PlanId;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.joda.time.DateTime;

import java.io.Serializable;

/**
 * Created by ASUS on 01-Dec-15.
 */
@Getter
@NoArgsConstructor
@ToString
public class PlanUpdateWithdrawalEvent  implements Serializable {

    private PlanId planId;

    private DateTime withDrawlDate;

    public PlanUpdateWithdrawalEvent(PlanId planId, DateTime withDrawlDate) {
        this.planId = planId;
        this.withDrawlDate = withDrawlDate;
    }

}
