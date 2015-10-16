package com.pla.grouplife.sharedresource.model;

import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Samir on 8/5/2015.
 */
public enum GLEndorsementType {

    FREE_COVER_LIMIT("Free Cover Limit") {
        @Override
        public List<GLEndorsementExcelHeader> getAllowedExcelHeaders() {
            return null;
        }
    }, ASSURED_MEMBER_ADDITION("Member Addition") {
        @Override
        public List<GLEndorsementExcelHeader> getAllowedExcelHeaders() {
            return Arrays.asList(GLEndorsementExcelHeader.MAN_NUMBER, GLEndorsementExcelHeader.NRC_NUMBER, GLEndorsementExcelHeader.CATEGORY, GLEndorsementExcelHeader.RELATIONSHIP
                    , GLEndorsementExcelHeader.SALUTATION, GLEndorsementExcelHeader.FIRST_NAME, GLEndorsementExcelHeader.LAST_NAME, GLEndorsementExcelHeader.GENDER,
                    GLEndorsementExcelHeader.DATE_OF_BIRTH, GLEndorsementExcelHeader.OCCUPATION, GLEndorsementExcelHeader.NO_OF_ASSURED,
                    GLEndorsementExcelHeader.ANNUAL_INCOME, GLEndorsementExcelHeader.MAIN_ASSURED_CLIENT_ID);
        }
    }, ASSURED_MEMBER_DELETION("Member Deletion") {
        @Override
        public List<GLEndorsementExcelHeader> getAllowedExcelHeaders() {
            return Arrays.asList(GLEndorsementExcelHeader.CATEGORY, GLEndorsementExcelHeader.RELATIONSHIP
                    , GLEndorsementExcelHeader.NO_OF_ASSURED, GLEndorsementExcelHeader.CLIENT_ID);
        }
    }, MEMBER_PROMOTION("Promotion") {
        @Override
        public List<GLEndorsementExcelHeader> getAllowedExcelHeaders() {
            return Arrays.asList(GLEndorsementExcelHeader.CLIENT_ID, GLEndorsementExcelHeader.OLD_ANNUAL_INCOME, GLEndorsementExcelHeader.NEW_ANNUAL_INCOME);
        }
    },
    NEW_CATEGORY_RELATION("Introduction of New category") {
        @Override
        public List<GLEndorsementExcelHeader> getAllowedExcelHeaders() {
            return Arrays.asList(GLEndorsementExcelHeader.PROPOSER_NAME, GLEndorsementExcelHeader.MAN_NUMBER, GLEndorsementExcelHeader.NRC_NUMBER, GLEndorsementExcelHeader.CATEGORY,
                    GLEndorsementExcelHeader.RELATIONSHIP, GLEndorsementExcelHeader.SALUTATION, GLEndorsementExcelHeader.FIRST_NAME, GLEndorsementExcelHeader.LAST_NAME,
                    GLEndorsementExcelHeader.DATE_OF_BIRTH, GLEndorsementExcelHeader.GENDER, GLEndorsementExcelHeader.OCCUPATION, GLEndorsementExcelHeader.NO_OF_ASSURED,
                    GLEndorsementExcelHeader.ANNUAL_INCOME, GLEndorsementExcelHeader.INCOME_MULTIPLIER, GLEndorsementExcelHeader.PLAN, GLEndorsementExcelHeader.SUM_ASSURED, GLEndorsementExcelHeader.PLAN_PREMIUM);
        }
    }, CHANGE_POLICY_HOLDER_NAME("Correction of Name-Policyholder") {
        @Override
        public List<GLEndorsementExcelHeader> getAllowedExcelHeaders() {
            return null;
        }
    }, CHANGE_ASSURED_NAME("Correction of Name - Assured") {
        @Override
        public List<GLEndorsementExcelHeader> getAllowedExcelHeaders() {
            return Arrays.asList(GLEndorsementExcelHeader.CLIENT_ID, GLEndorsementExcelHeader.SALUTATION, GLEndorsementExcelHeader.FIRST_NAME, GLEndorsementExcelHeader.LAST_NAME);
        }
    },
    CHANGE_POLICY_HOLDER_CONTACT_DETAIL("Change of Address") {
        @Override
        public List<GLEndorsementExcelHeader> getAllowedExcelHeaders() {
            return null;
        }
    }, CHANGE_DOB("Change Life Assured Date of Birth") {
        @Override
        public List<GLEndorsementExcelHeader> getAllowedExcelHeaders() {
            return Arrays.asList(GLEndorsementExcelHeader.CLIENT_ID, GLEndorsementExcelHeader.DATE_OF_BIRTH);
        }
    }, CHANGE_NRC("Correction Life Assured - NRC") {
        @Override
        public List<GLEndorsementExcelHeader> getAllowedExcelHeaders() {
            return Arrays.asList(GLEndorsementExcelHeader.CLIENT_ID, GLEndorsementExcelHeader.NRC_NUMBER);
        }
    },
    CHANGE_MAN_NUMBER("Correction Life Assured-MAN Number") {
        @Override
        public List<GLEndorsementExcelHeader> getAllowedExcelHeaders() {
            return Arrays.asList(GLEndorsementExcelHeader.CLIENT_ID, GLEndorsementExcelHeader.MAN_NUMBER);
        }
    }, CHANGE_GENDER("Correction Life Assured - Gender") {
        @Override
        public List<GLEndorsementExcelHeader> getAllowedExcelHeaders() {
            return Arrays.asList(GLEndorsementExcelHeader.CLIENT_ID, GLEndorsementExcelHeader.GENDER);
        }
    };

    private String description;

    GLEndorsementType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /*
    * @TODO please review @Samir
    * */
  /*  public static List<Map<String, String>> getAllEndorsementTypeExceludingFCL() {
        List<Map<String, String>> endorsementTypes = Lists.newArrayList();
        GLEndorsementType[] glEndorsementTypes = GLEndorsementType.values();
        for (int count = 0; count < glEndorsementTypes.length; count++) {
            GLEndorsementType glEndorsementType = glEndorsementTypes[count];
            if (!GLEndorsementType.FREE_COVER_LIMIT.equals(glEndorsementType)) {
                Map<String, String> map = new HashMap<>();
                map.put("code", glEndorsementType.name());
                map.put("description", glEndorsementType.getDescription());
                endorsementTypes.add(map);
            }
        }
        return endorsementTypes;
    }*/

    public static List<Map<String, String>> getAllEndorsementType() {
        List<Map<String, String>> endorsementTypes = Lists.newArrayList();
        GLEndorsementType[] glEndorsementTypes = GLEndorsementType.values();
        for (int count = 0; count < glEndorsementTypes.length; count++) {
            GLEndorsementType glEndorsementType = glEndorsementTypes[count];
            Map<String, String> map = new HashMap<>();
            map.put("code", glEndorsementType.name());
            map.put("description", glEndorsementType.getDescription());
            endorsementTypes.add(map);
        }
        return endorsementTypes;
    }

    public abstract List<GLEndorsementExcelHeader> getAllowedExcelHeaders();
}
