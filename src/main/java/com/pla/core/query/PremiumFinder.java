package com.pla.core.query;

import com.pla.core.domain.model.plan.premium.Premium;
import com.pla.publishedlanguage.domain.model.PremiumCalculationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Samir on 4/12/2015.
 */
@Service
public class PremiumFinder {

    private MongoTemplate mongoTemplate;

    @Autowired
    public PremiumFinder(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public Premium findPremium(PremiumCalculationDto premiumCalculationDto) {
        Criteria premiumCriteria = Criteria.where("planId").is(premiumCalculationDto.getPlanId())
                .and("effectiveFrom").lte(premiumCalculationDto.getCalculateAsOf().toDate()).and("validTill").is(null);
        if (premiumCalculationDto.getCoverageId() != null) {
            premiumCriteria.and("coverageId.coverageId").is(premiumCalculationDto.getCoverageId().getCoverageId());
        }
        Query query = new Query(premiumCriteria);
        List<Premium> premiums = mongoTemplate.find(query, Premium.class);
        checkArgument(isNotEmpty(premiums), "Premium cannot be computed as no premium setup found");
        checkArgument(premiums.size() == 1);
        return premiums.get(0);
    }
}
