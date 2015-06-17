package com.pla.publishedlanguage.contract;

import com.google.common.base.Preconditions;
import com.pla.publishedlanguage.domain.model.EmployeeDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Admin on 3/26/2015.
 */
@Service(value = "smeGateway")
public class SMEGatewayImpl implements ISMEGateway {


    @Value("${spring.smeServer.${spring.profiles.active}.url}")
    private String serverUrl;

    @Override
    public EmployeeDto getEmployeeDetailByIdOrByNRCNumber(String employeeId, String nrcNumber) {
        String url = serverUrl;
        Preconditions.checkNotNull(serverUrl);
        RestTemplate restTemplate = new RestTemplate();
        nrcNumber = isNotEmpty(nrcNumber) ? nrcNumber.replaceAll("/", "").trim() : nrcNumber;
        String employeeDetailByIDAndNRCNumberURL = employeeId != null ? url + "/getemployee?employeeId=" + employeeId : url + "/getemployee?nrcNumber=" + nrcNumber;
        EmployeeDto employeeDetail = restTemplate.getForObject(employeeDetailByIDAndNRCNumberURL, EmployeeDto.class);
        Preconditions.checkNotNull(employeeDetail);
        return employeeDetail;
    }

    @Override
    public List<EmployeeDto> getEmployeeDetailByDesignation(String designation) {
        Preconditions.checkNotNull(serverUrl);
        String url = serverUrl;
        RestTemplate restTemplate = new RestTemplate();
        String employeeDetailByDesignationURL = url + "/getemployeebydesignation?designation=" + designation;
        List<Map<String, Object>> listOfEmployeeDetails = restTemplate.getForObject(employeeDetailByDesignationURL, List.class);
        List<EmployeeDto> employeeDtos = listOfEmployeeDetails.stream().map(new Function<Map<String, Object>, EmployeeDto>() {
            @Override
            public EmployeeDto apply(Map<String, Object> map) {
                EmployeeDto employeeDto = new EmployeeDto((String) map.get("firstName"), (String) map.get("lastName"),
                        (String) map.get("middleName"), (String) map.get("employeeId"), (String) map.get("nrcNumber"),
                        (String) map.get("designation"), (String) map.get("designationDescription"), (String) map.get("departmentName"));
                return employeeDto;
            }
        }).collect(Collectors.toList());
        return employeeDtos;
    }


}
