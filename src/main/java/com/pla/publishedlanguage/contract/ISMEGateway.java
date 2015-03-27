package com.pla.publishedlanguage.contract;

import com.pla.publishedlanguage.domain.model.EmployeeDto;

import java.util.List;

/**
 * Created by Admin on 3/26/2015.
 */
public interface ISMEGateway {

    EmployeeDto getEmployeeDetailByIdOrByNRCNumber(String employeeId,String NRCNumber);

    List<EmployeeDto> getEmployeeDetailByDesignation(String designation);
}
