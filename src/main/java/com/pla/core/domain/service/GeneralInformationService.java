package com.pla.core.domain.service;

import com.pla.core.domain.model.Admin;
import com.pla.core.domain.model.generalinformation.OrganizationGeneralInformation;
import com.pla.core.domain.model.generalinformation.ProductLineGeneralInformation;
import com.pla.core.dto.GeneralInformationDto;
import com.pla.sharedkernel.domain.model.DiscountFactorItem;
import com.pla.sharedkernel.domain.model.ModalFactorItem;
import com.pla.sharedkernel.domain.model.Tax;
import com.pla.sharedkernel.identifier.LineOfBusinessId;
import org.nthdimenzion.common.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Created by Admin on 4/1/2015.
 */
@Service
public class GeneralInformationService {

    private AdminRoleAdapter adminRoleAdapter;

    private MongoTemplate springMongoTemplate;

    @Autowired
    public GeneralInformationService(AdminRoleAdapter adminRoleAdapter, MongoTemplate springMongoTemplate) {
        this.adminRoleAdapter = adminRoleAdapter;
        this.springMongoTemplate = springMongoTemplate;
    }

    public boolean createProductLineInformation(LineOfBusinessId lineOfBusinessId, UserDetails userDetails, GeneralInformationDto generalInformationDto) {
        Admin admin = adminRoleAdapter.userToAdmin(userDetails);
        ProductLineGeneralInformation productLineGeneralInformation = admin.createProductLineGeneralInformation(lineOfBusinessId, generalInformationDto);
        springMongoTemplate.save(productLineGeneralInformation);
        return AppConstants.SUCCESS;
    }

    public boolean createOrganizationInformation(List<Map<ModalFactorItem, BigDecimal>> modelFactorItems, List<Map<DiscountFactorItem, BigDecimal>> discountFactorItems, Map<Tax, BigDecimal> serviceTax, UserDetails userDetails) {
        Admin admin = adminRoleAdapter.userToAdmin(userDetails);
        OrganizationGeneralInformation organizationGeneralInformation = admin.createOrganizationGeneralInformation(modelFactorItems, discountFactorItems, serviceTax);
        springMongoTemplate.save(organizationGeneralInformation);
        return AppConstants.SUCCESS;
    }

    public Boolean updateOrganizationInformation(GeneralInformationDto generalInformationDto, UserDetails userDetails) {
        Admin admin = adminRoleAdapter.userToAdmin(userDetails);
        Query findGeneralInformation = new Query();
        Update update = new Update();
        findGeneralInformation.addCriteria(Criteria.where("organizationInformationId").is(generalInformationDto.getOrganizationInformationId()));
        OrganizationGeneralInformation organizationGeneralInformation = springMongoTemplate.findOne(findGeneralInformation, OrganizationGeneralInformation.class);
        checkArgument(organizationGeneralInformation != null);
        organizationGeneralInformation = admin.updateOrganizationInformation(organizationGeneralInformation, generalInformationDto);
        update.set("modelFactorItems", organizationGeneralInformation.getModelFactorItems());
        update.set("discountFactorItems", organizationGeneralInformation.getDiscountFactorItems());
        update.set("serviceTax", organizationGeneralInformation.getServiceTax());
        springMongoTemplate.updateFirst(findGeneralInformation, update, OrganizationGeneralInformation.class);
        return AppConstants.SUCCESS;
    }

    public Boolean updateProductLineInformation(GeneralInformationDto generalInformationDto, UserDetails userDetails) {
        Admin admin = adminRoleAdapter.userToAdmin(userDetails);
        Update update = new Update();
        Query findGeneralInformation = new Query();
        findGeneralInformation.addCriteria(Criteria.where("productLineInformationId").is(generalInformationDto.getProductLineInformationId()));
        ProductLineGeneralInformation productLineGeneralInformation = springMongoTemplate.findOne(findGeneralInformation, ProductLineGeneralInformation.class);
        checkArgument(productLineGeneralInformation != null);
        productLineGeneralInformation = admin.updateProductLineInformation(productLineGeneralInformation, generalInformationDto);
        update = updateProductLineInformation(update, productLineGeneralInformation);
        springMongoTemplate.updateFirst(findGeneralInformation, update, ProductLineGeneralInformation.class);
        return AppConstants.SUCCESS;
    }

    private Update updateProductLineInformation(Update update, ProductLineGeneralInformation updatedProductLineInformation) {
        update.set("quotationProcessInformation", updatedProductLineInformation.getQuotationProcessInformation());
        update.set("enrollmentProcessInformation", updatedProductLineInformation.getEnrollmentProcessInformation());
        update.set("reinstatementProcessInformation", updatedProductLineInformation.getReinstatementProcessInformation());
        update.set("endorsementProcessInformation", updatedProductLineInformation.getEndorsementProcessInformation());
        update.set("claimProcessInformation", updatedProductLineInformation.getClaimProcessInformation());
        update.set("policyFeeProcessInformation", updatedProductLineInformation.getPolicyFeeProcessInformation());
        update.set("policyProcessMinimumLimit", updatedProductLineInformation.getPolicyProcessMinimumLimit());
        update.set("surrenderProcessInformation", updatedProductLineInformation.getSurrenderProcessInformation());
        update.set("maturityProcessInformation", updatedProductLineInformation.getMaturityProcessInformation());
        return update;
    }

}
