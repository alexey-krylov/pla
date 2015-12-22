package com.pla.grouplife.claim.domain.model;

import com.google.common.collect.Maps;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by ak
 */
public enum CauseOfDeathAccidental {
    YES("Yes"), NO("No");
    private String description;

    CauseOfDeathAccidental(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }

    public static List<Map<String, Object>> getCauseOfDeath() {
        return Arrays.asList(CauseOfDeathAccidental.values()).parallelStream().map(new Function<CauseOfDeathAccidental, Map<String, Object>>() {
            @Override
            public Map<String, Object> apply(CauseOfDeathAccidental causeOfDeath) {
                Map<String, Object> causeOfDeathMap = Maps.newLinkedHashMap();
                causeOfDeathMap.put("isDeathAccidental", causeOfDeath.name());
                causeOfDeathMap.put("description", causeOfDeath.toString());
                return causeOfDeathMap;
            }
        }).collect(Collectors.toList());
    }
}