package com.pla.core.presentation.controller;

import com.google.common.collect.Maps;
import com.pla.core.application.producrclaim.CreateProductClaimCommand;
import com.pla.core.application.producrclaim.UpdateProductClaimCommand;
import com.pla.core.domain.exception.MandatoryDocumentException;
import com.pla.core.dto.ProductClaimTypeDto;
import com.pla.core.query.ProductClaimMapperFinder;
import com.pla.sharedkernel.domain.model.ClaimType;
import com.pla.sharedkernel.identifier.LineOfBusinessEnum;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.nthdimenzion.presentation.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.nthdimenzion.presentation.AppUtils.getLoggedInUserDetail;

/**
 * Created by ASUS on 28-Aug-15.
 */
@Controller
@RequestMapping(value = "/core/productclaimmap")
public class ProductClaimSetUpController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductClaimSetUpController.class);

    @Autowired
    private ProductClaimMapperFinder productClaimMapperFinder;

    @Autowired
    private CommandGateway commandGateway;

    @RequestMapping(value = "/opencreateproductclaim",method = RequestMethod.GET)
    public ModelAndView openCreatePage(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/core/productClaim/createProductClaim");
        return modelAndView;
    }

    @RequestMapping(value = "/openviewproductclaim",method = RequestMethod.GET)
    public ModelAndView openViewPage(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/core/productClaim/viewProductClaim");
        return modelAndView;
    }

    @RequestMapping(value = "/getplanbylob/{lineOfBusiness}",method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String,Object>> getProcessByLineOfBusiness(@PathVariable("lineOfBusiness") LineOfBusinessEnum lineOfBusiness){
        return productClaimMapperFinder.getPlanDetailBy(lineOfBusiness);
    }

    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public ModelAndView searchQuotation(ProductClaimTypeDto searchIlDto) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("searchResult", productClaimMapperFinder.searchProductClaimMap(searchIlDto.getLineOfBusiness(),searchIlDto.getPlanName()));
        modelAndView.addObject("searchCriteria", searchIlDto);
        modelAndView.setViewName("pla/core/productClaim/viewProductClaim");
        return modelAndView;
    }


    @RequestMapping(value = "/getclaimtypebylob/{lineOfBusiness}",method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String,Object>> getClaimTypeByLineOfBusiness(@PathVariable("lineOfBusiness") LineOfBusinessEnum lineOfBusiness){
        return lineOfBusiness.getClaimTypes().parallelStream().map(new Function<ClaimType, Map<String,Object>>() {
            @Override
            public Map<String,Object> apply(ClaimType claimType) {
                Map<String,Object> claimTypeMap  = Maps.newLinkedHashMap();
                claimTypeMap.put("claimType",claimType.name());
                claimTypeMap.put("description",claimType.toString());
                return claimTypeMap;
            }
        }).collect(Collectors.toList());
    }



    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Result> createProductClaim(@RequestBody CreateProductClaimCommand createProductClaimCommand, BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity(Result.failure("Error in creating Product Claim Mapping", bindingResult.getAllErrors()), HttpStatus.OK);
        }
        /*
        *  check for duplicate product claim mapping
        * */
        boolean planCodeCount = productClaimMapperFinder.getNumberOfPlanCodeCount(createProductClaimCommand.getPlanCode());
        if (planCodeCount){
            LOGGER.error("Product Claim Record with this Plan Code already exist ..Try with another PlanCode");
            return new ResponseEntity(Result.failure("Product Claim Record with this Plan Code already exist ..Try with another PlanCode"), HttpStatus.OK);
        }
        try {
            UserDetails userDetails = getLoggedInUserDetail(request);
            createProductClaimCommand.setUserDetails(userDetails);
            commandGateway.sendAndWait(createProductClaimCommand);
        } catch (MandatoryDocumentException e) {
            LOGGER.error("Error in creating Product Claim Mapping", e);
            return new ResponseEntity(Result.failure("Error in creating Product Claim Mapping"), HttpStatus.OK);
        }
        return new ResponseEntity(Result.success("Product Claim Mapping created successfully"), HttpStatus.OK);
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Result> updateProductClaim(@RequestBody UpdateProductClaimCommand updateProductClaimCommand, BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity(Result.failure("Error in updating Product Claim Mapping", bindingResult.getAllErrors()), HttpStatus.OK);
        }
        try {
            UserDetails userDetails = getLoggedInUserDetail(request);
            updateProductClaimCommand.setUserDetails(userDetails);
            commandGateway.sendAndWait(updateProductClaimCommand);
        } catch (MandatoryDocumentException e) {
            LOGGER.error("Error in Updating Product Claim Mapping", e);
            return new ResponseEntity(Result.failure("Error in Updating Product Claim Mapping"), HttpStatus.OK);
        }
        return new ResponseEntity(Result.success("Product Claim Mapping Updated successfully"), HttpStatus.OK);
    }

    @RequestMapping(value = "/getallproductclaimmap",method = RequestMethod.GET)
    @ResponseBody
    public List<ProductClaimTypeDto> getProductClaimMappingDetail(){
        return productClaimMapperFinder.getAllProductClaimMapDetail();
    }

    @RequestMapping(value = "/getproductclaimbyid/{productClaimId}",method = RequestMethod.GET)
    @ResponseBody
    public ProductClaimTypeDto getProductClaimMappingDetailById(@PathVariable("productClaimId") String productClaimId){
        return productClaimMapperFinder.getProductClaimMapDetailById(productClaimId);
    }

     @RequestMapping(value="/search" ,method=RequestMethod.GET)
     @ResponseBody
         public List<ProductClaimTypeDto> search(@RequestParam(value = "lineOfBusiness",required = false) LineOfBusinessEnum lineOfBusiness, @RequestParam(value = "planName",required = false) String planName){
          return productClaimMapperFinder.searchProductClaimMap(lineOfBusiness, planName);
     }


}
