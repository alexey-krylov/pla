/*
 * Copyright (c) 3/30/15 9:26 AM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.repository;

import com.pla.core.domain.model.plan.Plan;
import com.pla.sharedkernel.identifier.PlanId;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author: Samir
 * @since 1.0 30/03/2015
 */
public interface PlanRepository extends MongoRepository<Plan, PlanId> {

}
