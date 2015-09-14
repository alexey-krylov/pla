package com.pla.individuallife.policy.presentation.model;

import com.google.common.collect.Maps;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Admin on 9/14/2015.
 */
public enum ILPolicyDocument {

    IL_POLICY_DOCUMENT("IL Policy Document");

    private String description;

    ILPolicyDocument(String description){
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }


    public static List<Map<String,Object>> getDeclaredPolicyDocument(){
        return Arrays.asList(ILPolicyDocument.values()).parallelStream().map(new Function<ILPolicyDocument, Map<String,Object>>() {
            @Override
            public Map<String, Object> apply(ILPolicyDocument ilPolicyDocument) {
                Map<String,Object> policyDocumentMap = Maps.newLinkedHashMap();
                policyDocumentMap.put("documentCode", ilPolicyDocument.name());
                policyDocumentMap.put("documentName", ilPolicyDocument.toString());
                return policyDocumentMap;
            }
        }).collect(Collectors.toList());
    }
}
