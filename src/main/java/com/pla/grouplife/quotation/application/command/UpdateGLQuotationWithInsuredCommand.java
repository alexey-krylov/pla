package com.pla.grouplife.quotation.application.command;

import com.pla.grouplife.quotation.query.InsuredDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

/**
 * Created by Samir on 5/19/2015.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateGLQuotationWithInsuredCommand {

    private String quotationId;

    private List<InsuredDto> insuredDtos;

    private UserDetails userDetails;

}
