package com.pla.grouplife.claim.domain.model;

import com.google.common.collect.Maps;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.function.Function;
/**
 * Created by ak
 */
public enum PoliceReportRegistered {
    YES("Yes"), NO("No");

    private String description;

    PoliceReportRegistered(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }

    public static List<Map<String, Object>> getPoliceReportRegistered() {
        return Arrays.asList(PoliceReportRegistered.values()).parallelStream().map(new Function<PoliceReportRegistered, Map<String, Object>>() {
            @Override
            public Map<String, Object> apply(PoliceReportRegistered policeReport) {
                Map<String, Object> policeReportMap = Maps.newLinkedHashMap();
                policeReportMap.put("policeReportRegistered", policeReport.name());
                policeReportMap.put("description", policeReport.toString());
                return policeReportMap;
            }
        }).collect(Collectors.toList());
    }
}


