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
@Service(value = "smeGateway")
public class SMEGatewayImpl implements ISMEGateway {


    @Value("${spring.smeServer.${spring.profiles.active}.url}")
    private String serverUrl;

    @Override
    public EmployeeDto getEmployeeDetailByIdOrByNRCNumber(String employeeId, String NRCNumber) {
        String url = serverUrl;
        Preconditions.checkNotNull(serverUrl);
        RestTemplate restTemplate = new RestTemplate();
        String employeeDetailByIDAndNRCNumberURL = url + "/getemployee?employeeId="+employeeId+"&nrcnumber="+NRCNumber;
        EmployeeDto employeeDetail = restTemplate.getForObject(employeeDetailByIDAndNRCNumberURL, EmployeeDto.class);
        Preconditions.checkNotNull(employeeDetail);
        return employeeDetail;
    }

    @Override
    public List<EmployeeDto> getEmployeeDetailByDesignation(String designation) {
        Preconditions.checkNotNull(serverUrl);
        String url = serverUrl;
        RestTemplate restTemplate = new RestTemplate();
        String employeeDetailByDesignationURL = url + "/getemployeebydesignation?designation="+designation;
        List<EmployeeDto> listOfEmployeeDetail = restTemplate.getForObject(employeeDetailByDesignationURL, List.class);
        Preconditions.checkNotNull(listOfEmployeeDetail);
        return listOfEmployeeDetail;
    }
}
