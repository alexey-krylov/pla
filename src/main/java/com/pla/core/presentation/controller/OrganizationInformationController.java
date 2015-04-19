package com.pla.core.presentation.controller;

import com.google.common.collect.Lists;
import com.pla.core.domain.exception.GeneralInformationException;
import com.pla.core.domain.service.GeneralInformationService;
import com.pla.core.dto.GeneralInformationDto;
import com.pla.sharedkernel.domain.model.DiscountFactorItem;
import com.pla.sharedkernel.domain.model.ModalFactorItem;
import com.pla.sharedkernel.domain.model.Tax;
import org.nthdimenzion.presentation.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static org.nthdimenzion.presentation.AppUtils.getLoggedInUSerDetail;

/**
 * Created by Admin on 4/8/2015.
 */
@Controller
@RequestMapping(value = "/core/organizationinformation",produces = MediaType.APPLICATION_JSON_VALUE,consumes = MediaType.ALL_VALUE)
public class OrganizationInformationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductLineInformationController.class);
    private GeneralInformationService generalInformationService;

    private MongoTemplate springMongoTemplate;

    @Autowired
    public OrganizationInformationController(GeneralInformationService generalInformationService, MongoTemplate springMongoTemplate){
        this.generalInformationService = generalInformationService;
        this.springMongoTemplate = springMongoTemplate;
    }

    @RequestMapping(value = "/getdefinedorganizationitem", method = RequestMethod.GET)
    @ResponseBody
    public List getOrganizationInformationItem(){
        List definedOrganizationInformationItem = Lists.newArrayList();
        definedOrganizationInformationItem.add(ModalFactorItem.values());
        definedOrganizationInformationItem.add(DiscountFactorItem.values());
        definedOrganizationInformationItem.add(Tax.values());
        return definedOrganizationInformationItem;
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public Result createOrganizationGeneralInformation(@RequestBody GeneralInformationDto generalInformationDto,BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return Result.failure("Error in creating Organization Information", bindingResult.getAllErrors());
        }
        try {
            checkArgument(generalInformationDto!=null);
            UserDetails userDetails = getLoggedInUSerDetail(request);
            generalInformationService.createOrganizationInformation(generalInformationDto.getModelFactorItems(), generalInformationDto.getDiscountFactorItems(), generalInformationDto.getServiceTax(), userDetails);
        }catch (GeneralInformationException e){
            LOGGER.debug(e.getMessage());
            return Result.failure(e.getMessage());
        }catch (Exception e){
            return Result.failure(e.getMessage());
        }
        return Result.success("Organization Information created successfully");
    }

    @RequestMapping(value = "/update")
    @ResponseBody
    public Result updateOrganizationInformation(@RequestBody GeneralInformationDto generalInformationDto,BindingResult bindingResult,HttpServletRequest  request) {
        if (bindingResult.hasErrors()) {
            return Result.failure("Error in Updating Organization Information", bindingResult.getAllErrors());
        }
        try {
            UserDetails userDetails = getLoggedInUSerDetail(request);
            generalInformationService.updateOrganizationInformation(generalInformationDto, userDetails);
        } catch (GeneralInformationException e){
            LOGGER.debug(e.getMessage());
            return Result.failure(e.getMessage());
        }catch (Exception e){
            return Result.failure(e.getMessage());
        }
        return Result.success("Organization information Updated successfully");
    }

    @RequestMapping(value = "/getorganizationformation", method = RequestMethod.GET)
    @ResponseBody
    public List<Map> getOrganizationInformation() {
        return springMongoTemplate.findAll(Map.class, "organization_information");
    }



}
