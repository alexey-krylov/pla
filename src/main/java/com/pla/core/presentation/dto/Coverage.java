/**
 * Created with IntelliJ IDEA.
 * User: Tejeswar
 * Date: 3/9/15
 * Time: 10:43 AM
 * To change this template use File | Settings | File Templates.
 */
package com.pla.core.presentation.dto;


import com.google.common.collect.Lists;
import lombok.Data;

import java.util.List;

@Data

public class Coverage {
    private String coverageName;
    private String description;
    List benefits = Lists.newArrayList("Death Benefit", "Accidental Death Benefit", "CI Benefit");
    private List<String> selectedBenefits = Lists.newArrayList();

}
