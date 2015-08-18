package com.pla.core.repository;

import com.pla.core.domain.model.generalinformation.ProductLineGeneralInformation;
import com.pla.sharedkernel.identifier.LineOfBusinessEnum;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by Samir on 8/18/2015.
 */
public interface ProductLineGeneralInformationRepository extends MongoRepository<ProductLineGeneralInformation, String> {

    public ProductLineGeneralInformation findByProductLine(LineOfBusinessEnum lineOfBusinessEnum);
}
