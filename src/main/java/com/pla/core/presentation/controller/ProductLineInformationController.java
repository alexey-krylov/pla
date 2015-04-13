package com.pla.core.presentation.controller;

import com.google.common.collect.Lists;
import com.pla.core.domain.exception.GeneralInformationException;
import com.pla.core.domain.service.GeneralInformationService;
import com.pla.core.dto.GeneralInformationDto;
import com.pla.sharedkernel.domain.model.PolicyFeeProcessType;
import com.pla.sharedkernel.domain.model.PolicyProcessMinimumLimitType;
import com.pla.sharedkernel.domain.model.ProductLineProcessType;
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
 * Created by Admin on 4/1/2015.
 */
@Controller
@RequestMapping(value = "/core/productlineinformation",produces = MediaType.APPLICATION_JSON_VALUE,consumes = MediaType.ALL_VALUE)
public class ProductLineInformationController {

    private static final Logger logger = LoggerFactory.getLogger(ProductLineInformationController.class);
    private GeneralInformationService generalInformationService;

    private MongoTemplate mongoTemplate;

    @Autowired
    public ProductLineInformationController(GeneralInformationService generalInformationService, MongoTemplate springMongoTemplate) {
        this.generalInformationService = generalInformationService;
        this.mongoTemplate = springMongoTemplate;
    }

    @RequestMapping(value = "/getproductlineinformationitem", method = RequestMethod.GET)
    @ResponseBody
    public List getProductLineInformationItem(){
        List definedProductLineInformationItem = Lists.newArrayList();
        definedProductLineInformationItem.add(PolicyProcessMinimumLimitType.values());
        definedProductLineInformationItem.add(ProductLineProcessType.values());
        definedProductLineInformationItem.add(PolicyFeeProcessType.values());
        return definedProductLineInformationItem;
    }


    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public Result createProductLineGeneralInformation(@RequestBody GeneralInformationDto generalInformationDto,BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return Result.failure("Error in creating Product Line Information", bindingResult.getAllErrors());
        }
        try {
            checkArgument(generalInformationDto!=null);
            UserDetails userDetails = getLoggedInUSerDetail(request);
            generalInformationService.createProductLineInformation(generalInformationDto.getLineOfBusinessId(), userDetails, generalInformationDto);
        }catch (GeneralInformationException e){
            logger.debug(e.getMessage());
            return Result.failure(e.getMessage());
        }catch (Exception e){
            return Result.failure(e.getMessage());
        }
        return Result.success("Product Line Information created successfully");
    }

    @RequestMapping(value = "/update")
    @ResponseBody
    public Result updateProductLineInformation(@RequestBody GeneralInformationDto generalInformationDto,BindingResult bindingResult,HttpServletRequest  request) {
        if (bindingResult.hasErrors()) {
            return Result.failure("Error in Updating Product Line Information", bindingResult.getAllErrors());
        }
        try {
            UserDetails userDetails = getLoggedInUSerDetail(request);
            generalInformationService.updateProductLineInformation(generalInformationDto, userDetails);
        } catch (GeneralInformationException e){
            logger.debug(e.getMessage());
            return Result.failure(e.getMessage());
        }catch (Exception e){
            return Result.failure(e.getMessage());
        }
        return Result.success("Product Line Information Updated successfully");
    }

    @RequestMapping(value = "/getproducrlineinformation", method = RequestMethod.GET)
    @ResponseBody
    public List<Map> getProductLineInformation() {
        return mongoTemplate.findAll(Map.class, "product_line_information");
    }

}