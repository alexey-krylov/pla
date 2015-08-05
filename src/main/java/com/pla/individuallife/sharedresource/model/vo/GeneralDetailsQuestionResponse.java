package com.pla.individuallife.sharedresource.model.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.DateTime;

import java.math.BigDecimal;

/**
 * Created by Karunakar on 7/6/2015.
 */
@Getter
@Setter
@NoArgsConstructor
public class GeneralDetailsQuestionResponse {
    private String name;
    private String policyOrProposalNumber;
    private BigDecimal amount;
    private DateTime date;
}
