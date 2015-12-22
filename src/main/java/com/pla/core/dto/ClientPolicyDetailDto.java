package com.pla.core.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by nthdimensioncompany on 13/10/2015.
 */
@Getter
@Setter
@NoArgsConstructor
public class ClientPolicyDetailDto {
    String number;
    String clientType;
    String underWriterDecision;
    String underWriterComments;
}
