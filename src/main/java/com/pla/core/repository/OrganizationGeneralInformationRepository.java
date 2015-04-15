package com.pla.core.repository;

import com.pla.core.domain.model.generalinformation.OrganizationGeneralInformation;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by Samir on 4/10/2015.
 */
public interface OrganizationGeneralInformationRepository extends MongoRepository<OrganizationGeneralInformation, String> {
}
