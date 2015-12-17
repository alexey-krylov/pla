package com.pla.core.hcp.domain.model;

import org.springframework.util.Assert;

import static org.springframework.util.Assert.*;

/**
 * Created by Mohan Sharma on 12/17/2015.
 */
public enum HCPCategory {
    L1("Level1"), L2("Level2"), L3("Level3"), HP("Health Post"), RC("Rural Health Centre"), UC("Urban Health Centre");

    public String description;

    HCPCategory(String description){
        this.description = description;
    }

    public static HCPCategory getHCPCategory(String description) {
        notNull(description, "description cannot be empty for HCPCategory");
        for (HCPCategory hcpCategory : values()) {
            if (hcpCategory.description.equalsIgnoreCase(description.trim())) {
                return hcpCategory;
            }
        }
        throw new IllegalArgumentException(description);
    }
}
