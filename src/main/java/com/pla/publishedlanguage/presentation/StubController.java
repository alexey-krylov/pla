/*
 * Copyright (c) 3/11/15 8:37 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.publishedlanguage.presentation;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.common.collect.Lists;
import com.pla.publishedlanguage.domain.model.EmployeeDto;
import org.nthdimenzion.security.service.UserLoginDetailDto;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * @author: Samir
 * @since 1.0 11/03/2015
 */
@Controller
@RequestMapping(value = "/stub", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
public class StubController {

    @RequestMapping(value = "/getuserdetail", method = RequestMethod.GET)
    @ResponseBody
    public UserLoginDetailDto getUserDetail() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        UserLoginDetailDto userLoginDetailDto = objectMapper.readValue(StubController.class.getResourceAsStream("/stubdata/userDetail.json"), UserLoginDetailDto.class);
        return userLoginDetailDto;
    }

    @RequestMapping(value = "/getemployee", method = RequestMethod.GET)
    @ResponseBody
    public EmployeeDto getEmployeeDetail(HttpServletRequest request,HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        String employeeId = request.getParameter("employeeId");
        String nrcNumber = request.getParameter("nrcnumber");
        if (employeeId == null && nrcNumber == null) {
            return null;
        }
        String jsonPath = "/stubdata/employeeDetail.json";
        List<EmployeeDto> listOfEmployeeDetail = getEmployeeDetail(jsonPath);
        EmployeeDto employeeDetail = new EmployeeDto();
        for (EmployeeDto employeeDetailFromJson : listOfEmployeeDetail) {
            if (employeeId.equals(employeeDetailFromJson.getEmployeeId()) || nrcNumber.equals(employeeDetailFromJson.getNrcNumber())) {
                employeeDetail = employeeDetailFromJson;
                break;
            }
        }
        return employeeDetail;
    }

    @RequestMapping(value = "/getemployeebydesignation", method = RequestMethod.GET)
    @ResponseBody
    public List<EmployeeDto> getEmployeeDetailByDesignation(HttpServletRequest request,HttpServletResponse response) throws IOException {

        String designation = request.getParameter("designation");
        if (designation == null) {
            return Collections.EMPTY_LIST;
        }
        List<EmployeeDto> employeeDetailByDesignation = Lists.newArrayList();
        String jsonPath = "/stubdata/employeeByDesignation.json";
        List<EmployeeDto> listOfEmployeeDetail = getEmployeeDetail(jsonPath);
        for (EmployeeDto employeeDto : listOfEmployeeDetail) {
            if (employeeDto.getDesignation().equals(designation)) {
                employeeDetailByDesignation.add(employeeDto);
            }
        }
        return employeeDetailByDesignation;
    }

    private List<EmployeeDto> getEmployeeDetail(String jsonPath) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        TypeFactory typeFactory = objectMapper.getTypeFactory();
        CollectionType employeeCollectionType = typeFactory.constructCollectionType(
                List.class, EmployeeDto.class);
        return objectMapper.readValue(StubController.class.getResourceAsStream(jsonPath), employeeCollectionType);
    }
}
