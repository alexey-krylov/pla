package com.pla.core.presentation.controller;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pla.core.domain.exception.GeneralInformationException;
import com.pla.core.domain.service.GeneralInformationService;
import com.pla.core.dto.GeneralInformationDto;
import com.pla.core.dto.ProductLineProcessDto;
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
import static org.nthdimenzion.utils.UtilValidator.isEmpty;

/**
 * Created by Admin on 4/1/2015.
 */
@Controller
@RequestMapping(value = "/core/productlineinformation",produces = MediaType.APPLICATION_JSON_VALUE,consumes = MediaType.ALL_VALUE)
public class ProductLineInformationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductLineInformationController.class);
    private GeneralInformationService generalInformationService;

    private MongoTemplate mongoTemplate;

    @Autowired
    public ProductLineInformationController(GeneralInformationService generalInformationService, MongoTemplate springMongoTemplate) {
        this.generalInformationService = generalInformationService;
        this.mongoTemplate = springMongoTemplate;
    }

    @RequestMapping(value = "/getproductlineprocessitem", method = RequestMethod.GET)
    @ResponseBody
    public  List<ProductLineProcessDto> getProductLineInformationItem(){
        return generalInformationService.getProductLineProcessItems();
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
            LOGGER.debug(e.getMessage());
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
            LOGGER.debug(e.getMessage());
            return Result.failure(e.getMessage());
        }catch (Exception e){
            return Result.failure(e.getMessage());
        }
        return Result.success("Product Line Information Updated successfully");
    }

    @RequestMapping(value = "/getproducrlineinformation", method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String,Object>> getProductLineInformation() {
        List<Map> productLineInformation =  mongoTemplate.findAll(Map.class, "product_line_information");
        if (isEmpty(productLineInformation)){
            return Lists.newArrayList();
        }
        List<Map<String,Object>> productLineInformationList = Lists.newArrayList();
        for (Map productLineInformationMap: productLineInformation){
            Map<String,Object> productLineInformationByBusinessId = Maps.newLinkedHashMap();
            productLineInformationByBusinessId.put("productLine",productLineInformationMap.get("productLine"));
            List listOfProcess = Lists.newArrayList();
            listOfProcess.add(productLineInformationMap.get("quotationProcessInformation"));
            listOfProcess.add(productLineInformationMap.get("enrollmentProcessInformation"));
            listOfProcess.add(productLineInformationMap.get("reinstatementProcessInformation"));
            listOfProcess.add(productLineInformationMap.get("endorsementProcessInformation"));
            listOfProcess.add(productLineInformationMap.get("claimProcessInformation"));
            listOfProcess.add(productLineInformationMap.get("policyFeeProcessInformation"));
            listOfProcess.add(productLineInformationMap.get("policyProcessMinimumLimit"));
            listOfProcess.add(productLineInformationMap.get("surrenderProcessInformation"));
            listOfProcess.add(productLineInformationMap.get("maturityProcessInformation"));
            productLineInformationByBusinessId.put("processType",listOfProcess);
            productLineInformationList.add(productLineInformationByBusinessId);
        }
        return productLineInformationList;
    }

}
