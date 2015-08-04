package com.pla.publishedlanguage.domain.model;

/**
 * Created by Samir on 4/7/2015.
 */
public enum PremiumFrequency {

    ANNUALLY {
        @Override
        public int getNoOfPremiumYearlyFactor() {
            return 1;
        }
    }, SEMI_ANNUALLY {
        @Override
        public int getNoOfPremiumYearlyFactor() {
            return 2;
        }
    }, QUARTERLY {
        @Override
        public int getNoOfPremiumYearlyFactor() {
            return 3;
        }
    }, MONTHLY {
        @Override
        public int getNoOfPremiumYearlyFactor() {
            return 0;
        }
    };

    public abstract int getNoOfPremiumYearlyFactor();
}
