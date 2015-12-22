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
public enum PostMortemAutopsyDone {
    YES("Yes"), NO("No");

    private String description;

    PostMortemAutopsyDone(String description) {
        this.description = description;
    }
    @Override
    public String toString() {
        return description;
    }
    public static List<Map<String, Object>> getPostMortemAutopsy() {
        return Arrays.asList(PostMortemAutopsyDone.values()).parallelStream().map(new Function<PostMortemAutopsyDone, Map<String, Object>>() {
            @Override
            public Map<String, Object> apply(PostMortemAutopsyDone postMortemAutopsyDone) {
                Map<String, Object> postMortemAutopsyMap = Maps.newLinkedHashMap();
                postMortemAutopsyMap.put("postMortemDone", postMortemAutopsyDone.name());
                postMortemAutopsyMap.put("description", postMortemAutopsyDone.toString());
                return postMortemAutopsyMap;
            }
        }).collect(Collectors.toList());
    }

}


