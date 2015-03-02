package com.pla.sample.springmvc.application;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Author: Nthdimenzion
 */

@Data
public class AgentCommand {

    @NotEmpty(message = "{agentNameEmpty}")
    private String agentName;

}
