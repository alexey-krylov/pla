package com.pla.core.hcp.repository;

import com.pla.core.hcp.domain.model.HCPRate;
import com.pla.core.hcp.domain.model.HCPRateId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

/**
 * Created by Mohan Sharma on 12/21/2015.
 */
public interface HCPRateRepository extends MongoRepository<HCPRate, HCPRateId> {

    @Query("{'hcpCode.hcpCode' : ?0 }")
    HCPRate findHCPRateByHCPCode(String hcpCode);
}
