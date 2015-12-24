package com.pla.core.query;

import com.pla.core.domain.model.plan.premium.Premium;
import com.pla.publishedlanguage.domain.model.PremiumCalculationDto;
import com.pla.sharedkernel.identifier.CoverageId;
import com.pla.sharedkernel.identifier.PlanId;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
        Premium premium = findPremium(premiumCalculationDto.getPlanId(), premiumCalculationDto.getCoverageId(), premiumCalculationDto.getCalculateAsOf());
        return premium;
    }

    public Premium findPremium(PlanId planId, CoverageId coverageId, LocalDate effectiveDate) {
        Criteria premiumCriteria = Criteria.where("planId").is(planId)
                .and("effectiveFrom").lte(effectiveDate.toDate()).and("validTill").is(null);
        if (coverageId != null) {
            premiumCriteria.and("coverageId.coverageId").is(coverageId.getCoverageId());
        } else {
            premiumCriteria.and("coverageId").is(null);
        }
        Query query = new Query(premiumCriteria);
        /*
        * Get the latest premium
        * */
        query.with(new Sort(Sort.Direction.DESC,"effectiveFrom"));
        List<Premium> premiums = mongoTemplate.find(query, Premium.class);
        checkArgument(isNotEmpty(premiums), "Premium cannot be computed as no premium setup found");
        /*checkArgument(premiums.size() == 1);*/
        return premiums.get(0);
    }

    public Set<String> findPlanAndCoveragePremiumDetail(){
        Criteria premiumCriteria = Criteria.where("effectiveFrom").lte(DateTime.now()).and("validTill").is(null);
        Query query = new Query(premiumCriteria);
        query.fields().include("planId").include("coverageId");
        List<Map> premiumPlan = mongoTemplate.find(query, Map.class, "premium");
        if (isNotEmpty(premiumPlan)){
            return premiumPlan.parallelStream().filter(new Predicate<Map>() {
                @Override
                public boolean test(Map map) {
                    return isNotEmpty(map.get("planId")!=null?map.get("planId").toString():"");
                }
            }).map(new Function<Map, String>() {
                @Override
                public String apply(Map map) {
                    return map.get("planId").toString();
                }
            }).collect(Collectors.toSet());
        }
        return Collections.EMPTY_SET;
    }
}
