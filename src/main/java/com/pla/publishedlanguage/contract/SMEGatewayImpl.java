package com.pla.publishedlanguage.contract;

import com.google.common.base.Preconditions;
import com.pla.publishedlanguage.domain.model.EmployeeDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Created by Admin on 3/26/2015.
 */
@Service
public class SMEGatewayImpl implements ISMEGateway {


    @Value("${spring.smeServer.${spring.profiles.active}.url}")
    private String serverUrl;

    @Override
    public EmployeeDto getEmployeeDetailByIdOrByNRCNumber(String employeeId, String NRCNumber) {
        Preconditions.checkNotNull(serverUrl);
        RestTemplate restTemplate = new RestTemplate();
        String employeeDetailByIDAndNRCNumberURL = serverUrl + "/getemployee?employeeId="+employeeId+"&nrcNumber="+NRCNumber;
        System.out.println(employeeDetailByIDAndNRCNumberURL);
        EmployeeDto  employeeDetail = restTemplate.getForObject(employeeDetailByIDAndNRCNumberURL, EmployeeDto.class);
        Preconditions.checkNotNull(employeeDetail);
        return employeeDetail;
    }

    @Override
    public List<EmployeeDto> getEmployeeDetailByDesignation(String designation) {
        Preconditions.checkNotNull(serverUrl);
        RestTemplate restTemplate = new RestTemplate();
        String employeeDetailByDesignationURL = serverUrl + "/getemployeebydesignation?designation="+designation;
        System.out.println(employeeDetailByDesignationURL);
        List<EmployeeDto> listOfEmployeeDetail = restTemplate.getForObject(employeeDetailByDesignationURL, List.class);
        Preconditions.checkNotNull(listOfEmployeeDetail);
        return listOfEmployeeDetail;
    }
}
