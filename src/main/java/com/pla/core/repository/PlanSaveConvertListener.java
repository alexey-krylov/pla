package com.pla.core.repository;

import com.mongodb.DBObject;
import com.pla.core.domain.model.plan.Plan;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;

/**
 * Created by pradyumna on 11-04-2015.
 */
public class PlanSaveConvertListener extends AbstractMongoEventListener<Plan> {
    @Override
    public void onBeforeConvert(Plan source) {
        super.onBeforeConvert(source);
    }

    @Override
    public void onBeforeSave(Plan source, DBObject dbo) {
        super.onBeforeSave(source, dbo);
    }
}
