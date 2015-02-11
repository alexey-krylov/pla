package com.pla.sample.springmvc.presentation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.joda.money.Money;
import org.joda.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class Person {

    private int id;
    private String name;
    private String branch;
    private String designation;
    private LocalDate joiningDate;
    private Money commission;
    private boolean status;

}

