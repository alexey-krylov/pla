package com.pla.grouphealth.policy.saga;

import org.axonframework.saga.annotation.AbstractAnnotatedSaga;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.springframework.stereotype.Component;

/**
 * Created by Samir on 7/15/2015.
 */
@Component
public class GroupHealthPolicySaga extends AbstractAnnotatedSaga {

    public static void main(String[] args) {
        Period period = new Period();
        period.plusDays(30);
        DateTime dateTime = new DateTime(2015,3,31,0,1);
        DateTime nextMonthDateTime= dateTime.plusMonths(1);
        dateTime.getDayOfMonth();
        System.out.println(""+dateTime);
    }
}
