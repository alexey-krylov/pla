package com.pla.core.domain.model.notification;

import com.google.common.collect.ImmutableMap;
import com.pla.sharedkernel.identifier.LineOfBusinessEnum;

/**
 * Created by Admin on 6/26/2015.
 */
public class NotificationRoleResolver {

    public static ImmutableMap<String,String> ghRoleMap = ImmutableMap.of("QUOTATION_PREPROCESSOR","ROLE_GROUP_HEALTH_QUOTATION_PROCESSOR","PROPOSAL_PREPROCESSOR","ROLE_GROUP_HEALTH_PROPOSAL_PROCESSOR");
    public static ImmutableMap<String,String> glRoleMap = ImmutableMap.of("QUOTATION_PREPROCESSOR","ROLE_GROUP_LIFE_QUOTATION_PROCESSOR","PROPOSAL_PREPROCESSOR","ROLE_GROUP_LIFE_PROPOSAL_PROCESSOR");
    public static ImmutableMap<String,String> ilRoleMap = ImmutableMap.of("QUOTATION_PREPROCESSOR","ROLE_INDIVIDUAL_LIFE_QUOTATION_PROCESSOR","PROPOSAL_PREPROCESSOR","ROLE_INDIVIDUAL_LIFE_PROPOSAL_PROCESSOR");
    public static ImmutableMap<LineOfBusinessEnum,ImmutableMap<String,String>> predefineRoleMap = ImmutableMap.of(LineOfBusinessEnum.GROUP_HEALTH, ghRoleMap,LineOfBusinessEnum.GROUP_LIFE, glRoleMap,
            LineOfBusinessEnum.INDIVIDUAL_LIFE, ilRoleMap);

    public static String notificationRoleResolver(LineOfBusinessEnum lineOfBusiness,String uiRole){
        return predefineRoleMap.get(lineOfBusiness).get(uiRole);
    }



}
