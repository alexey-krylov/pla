package com.pla.core.domain.model.notification;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.pla.sharedkernel.identifier.LineOfBusinessEnum;

import java.util.Map;

/**
 * Created by Admin on 6/26/2015.
 */
public class NotificationRoleMapper {

    public static Map<String,Object> getRoleTypeDetailByLineOfBusiness(LineOfBusinessEnum lineOfBusiness){
        Map<String,Object>  roleMap = Maps.newLinkedHashMap();
        switch (lineOfBusiness){
            case GROUP_LIFE:
                roleMap.put("roleType","GROUP_LIFE_QUOTATION_PROCESSOR_ROLE");
                roleMap.put("description","Group Life Quotation Processor");
                break;
            case GROUP_HEALTH:
                roleMap.put("roleType","GROUP_HEALTH_QUOTATION_PROCESSOR_ROLE");
                roleMap.put("description","Group Health Quotation Processor");
                break;
            case INDIVIDUAL_LIFE:
                roleMap.put("roleType","INDIVIDUAL_LIFE_QUOTATION_PROCESSOR_ROLE");
                roleMap.put("description","Individual Life Quotation Processor");
                break;
        }
        return roleMap;
    }

    public static String getRoleTypeByLineOfBusiness(LineOfBusinessEnum lineOfBusiness){
        return ImmutableMap.of(LineOfBusinessEnum.GROUP_LIFE,"ROLE_GROUP_LIFE_QUOTATION_PROCESSOR",LineOfBusinessEnum.GROUP_HEALTH,"ROLE_GROUP_HEALTH_QUOTATION_PROCESSOR",
                LineOfBusinessEnum.INDIVIDUAL_LIFE,"ROLE_INDIVIDUAL_LIFE_QUOTATION_PROCESSOR").get(lineOfBusiness);

    }

}
