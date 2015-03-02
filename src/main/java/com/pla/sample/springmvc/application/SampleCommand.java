package com.pla.sample.springmvc.application;

import com.google.common.collect.Lists;
import lombok.Data;
import org.hibernate.validator.constraints.Email;
import org.joda.money.Money;
import org.joda.time.LocalDate;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Past;
import java.util.List;

/**
 * Author: Nthdimenzion
 */

@Data
public class SampleCommand {

    @Email(message = "{invalidEmail}")
    private String email="sudarshan89@gmail.com";

    private String password;

    private Boolean active;

    private String gender="F";

    @Past(message = "{onlyPastDates}")
    private LocalDate dateOfBirth;

    private  List<String> choicesList = Lists.newArrayList("choiceOne","choiceTwo","choiceThree");

    private  List<String> countries = Lists.newArrayList("India","Pakistan","Nepal","Bangladesh");

    private  List<String> selectedList = Lists.newArrayList();

    private  String selectedCountry;

    private Money commission;

    private MultipartFile multipartFile;

}
