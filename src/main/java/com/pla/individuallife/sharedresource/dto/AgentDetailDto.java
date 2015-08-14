package com.pla.individuallife.sharedresource.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Created by Karunakar on 6/24/2015.
 */
@Getter
@AllArgsConstructor
@EqualsAndHashCode(of = {"agentId"})
@NoArgsConstructor
public class AgentDetailDto {
    private String agentId;
    private String firstName;
    private BigDecimal commission;
}
