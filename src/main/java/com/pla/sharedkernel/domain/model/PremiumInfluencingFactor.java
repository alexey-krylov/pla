/*
 * Copyright (c) 3/25/15 9:06 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.sharedkernel.domain.model;

import com.pla.core.domain.model.plan.Plan;
import com.pla.sharedkernel.identifier.CoverageId;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.nthdimenzion.utils.UtilValidator.isEmpty;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * @author: Samir
 * @since 1.0 25/03/2015
 */
public enum PremiumInfluencingFactor {

    SUM_ASSURED("Sum Assured") {
        @Override
        public String[] getAllowedValues(Plan plan, CoverageId coverageId) {
            List<BigDecimal> sumAssureValues = null;
            if (coverageId.getCoverageId() != null) {
                sumAssureValues = plan.getAllowedCoverageSumAssuredValues(coverageId);
            } else {
                sumAssureValues = plan.getAllowedSumAssuredValues();
            }
            Collections.sort(sumAssureValues);
            String[] data = new String[sumAssureValues.size()];
            for (int count = 0; count < sumAssureValues.size(); count++) {
                data[count] = sumAssureValues.get(count).toString();
            }
            return data;
        }

        @Override
        public boolean isValidValue(Plan plan, CoverageId coverageId, String value) {
            return isEmpty(value) ? false : coverageId.getCoverageId() == null ? plan.isValidSumAssured(BigDecimal.valueOf(Double.valueOf(value.trim()).longValue())) : plan.isValidCoverageSumAssured(BigDecimal.valueOf(Double.valueOf(value.trim()).longValue()), coverageId);
        }

        @Override
        public String getErrorMessage(String value) {
            return value + " :is not valid Sum Assured for the selected plan/coverage.";
        }
    }, AGE("Age") {
        @Override
        public String[] getAllowedValues(Plan plan, CoverageId coverageId) {
            List<Integer> ages = null;
            if (coverageId.getCoverageId() != null) {
                ages = plan.getAllowedCoverageAges(coverageId);
            } else {
                ages = plan.getAllowedAges();
            }
            plan.getAllowedAges();
            Collections.sort(ages);
            String[] data = new String[ages.size()];
            for (int count = 0; count < ages.size(); count++) {
                data[count] = ages.get(count).toString();
            }
            return data;
        }

        @Override
        public boolean isValidValue(Plan plan, CoverageId coverageId, String value) {
            return isEmpty(value) ? false : coverageId.getCoverageId() == null ? plan.isValidAge(Integer.valueOf(value.trim())) : plan.isValidCoverageAge(Integer.valueOf(value.trim()), coverageId);
        }

        @Override
        public String getErrorMessage(String value) {
            return value + " :is not valid Age for the selected plan/coverage.";
        }
    }, POLICY_TERM("Policy Term") {
        @Override
        public String[] getAllowedValues(Plan plan, CoverageId coverageId) {
            List<Integer> policyTerms = new ArrayList<>();
            if (coverageId.getCoverageId() != null) {
                policyTerms.addAll(plan.getAllowedCoverageTerm(coverageId));
            } else {
                policyTerms.addAll(plan.getAllowedPolicyTerm());
            }
            Collections.sort(policyTerms);
            String[] data = new String[policyTerms.size()];
            for (int count = 0; count < policyTerms.size(); count++) {
                data[count] = policyTerms.get(count).toString();
            }
            return data;
        }

        @Override
        public boolean isValidValue(Plan plan, CoverageId coverageId, String value) {
            return isEmpty(value) ? false : coverageId.getCoverageId() == null ? plan.isValidPolicyTerm(Integer.valueOf(value.trim())) : plan.isValidCoverageTerm(Integer.valueOf(value.trim()), coverageId);
        }

        @Override
        public String getErrorMessage(String value) {
            return value + " :is not valid Policy Term for the selected plan/coverage.";
        }
    }, PREMIUM_PAYMENT_TERM("Premium Payment Term") {
        @Override
        public String[] getAllowedValues(Plan plan, CoverageId coverageId) {
            List<Integer> premiumTerms = new ArrayList<>();
            premiumTerms.addAll(plan.getAllowedPremiumTerms());
            Collections.sort(premiumTerms);
            String[] data = new String[premiumTerms.size()];
            for (int count = 0; count < premiumTerms.size(); count++) {
                data[count] = premiumTerms.get(count).toString();
            }
            return data;
        }

        @Override
        public boolean isValidValue(Plan plan, CoverageId coverageId, String value) {
            return isEmpty(value) ? false : plan.isValidPremiumTerm(Integer.valueOf(value.trim()));
        }

        @Override
        public String getErrorMessage(String value) {
            return value + " :is not valid Premium Payment Term for the selected plan/coverage.";
        }
    }, GENDER("Gender") {
        @Override
        public String[] getAllowedValues(Plan plan, CoverageId coverageId) {
            return new String[]{Gender.MALE.name(), Gender.FEMALE.name()};
        }

        @Override
        public boolean isValidValue(Plan plan, CoverageId coverageId, String value) {
            return isEmpty(value) ? false : (value.trim().equals(Gender.MALE.name()) || value.trim().equals(Gender.FEMALE.name()));
        }

        @Override
        public String getErrorMessage(String value) {
            return value + " :is not valid Gender.";
        }
    }, SMOKING_STATUS("Smoking Status") {
        @Override
        public String[] getAllowedValues(Plan plan, CoverageId coverageId) {
            return new String[]{SmokingStatus.YES.name(), SmokingStatus.NO.name()};
        }

        @Override
        public boolean isValidValue(Plan plan, CoverageId coverageId, String value) {
            return isEmpty(value) ? false : (value.trim().equals(SmokingStatus.YES.name()) || value.trim().equals(SmokingStatus.NO.name()));
        }

        @Override
        public String getErrorMessage(String value) {
            return value + " :is not valid Smoking Status.";
        }
    },
    INDUSTRY("Industry") {
        @Override
        public String[] getAllowedValues(Plan plan, CoverageId coverageId) {
            return new String[]{"Nth Dimension"};
        }

        @Override
        public boolean isValidValue(Plan plan, CoverageId coverageId, String value) {
            return isNotEmpty(value);
        }

        @Override
        public String getErrorMessage(String value) {
            return value + " :is not valid Industry.";
        }
    }, DESIGNATION("Designation") {
        @Override
        public String[] getAllowedValues(Plan plan, CoverageId coverageId) {
            return new String[]{"Software Engineer"};
        }

        @Override
        public boolean isValidValue(Plan plan, CoverageId coverageId, String value) {
            return isNotEmpty(value);
        }

        @Override
        public String getErrorMessage(String value) {
            return value + " :is not valid Designation";
        }
    }, OCCUPATION_CATEGORY("Occupation Category") {
        @Override
        public String[] getAllowedValues(Plan plan, CoverageId coverageId) {
            return new String[]{"Developer"};
        }

        @Override
        public boolean isValidValue(Plan plan, CoverageId coverageId, String value) {
            return isNotEmpty(value);
        }

        @Override
        public String getErrorMessage(String value) {
            return value + " :is not valid Occupation category";
        }
    }, BMI("BMI") {
        @Override
        public String[] getAllowedValues(Plan plan, CoverageId coverageId) {
            return new String[]{""};
        }

        @Override
        public boolean isValidValue(Plan plan, CoverageId coverageId, String value) {
            return isNotEmpty(value);
        }

        @Override
        public String getErrorMessage(String value) {
            return " ";
        }
    }, INCOME_MULTIPLIER("Income Multiplier") {
        @Override
        public String[] getAllowedValues(Plan plan, CoverageId coverageId) {
            return new String[0];
        }

        @Override
        public boolean isValidValue(Plan plan, CoverageId coverageId, String value) {
            return isNotEmpty(value);
        }

        @Override
        public String getErrorMessage(String value) {
            return " ";
        }
    };

    private String description;

    PremiumInfluencingFactor(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public abstract String[] getAllowedValues(Plan plan, CoverageId coverageId);

    public abstract boolean isValidValue(Plan plan, CoverageId coverageId, String value);

    public abstract String getErrorMessage(String value);
}
