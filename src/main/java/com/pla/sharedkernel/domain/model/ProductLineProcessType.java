package com.pla.sharedkernel.domain.model;

import lombok.Getter;

/**
 * Created by Admin on 4/1/2015.
 */
@Getter
public enum ProductLineProcessType {
    PURGE_TIME_PERIOD("Purge Time Period (Days)"){
        @Override
        public String getFullDescriptionByProcess(String process, String status) {
            return "Number of days after which "+process+" should be purged";
        }
    },
    FIRST_REMAINDER("First Reminder (Days)"){
        @Override
        public String getFullDescriptionByProcess(String process, String status) {
            return "First Reminder to be triggered after specified number of Days";
        }
    },
    SECOND_REMAINDER("Second Reminder (Days)"){
        @Override
        public String getFullDescriptionByProcess(String process, String status) {
            return "Second Reminder to be triggered  specified number of days after First Reminder";
        }
    },
    LAPSE("Lapse (Days)"){
        @Override
        public String getFullDescriptionByProcess(String process, String status) {
            return "Number of days after Second reminder  post which  New Business Policy Status should be lapsed and status should be converted to ' Lapsed' ";
        }
    },
    CLOSURE("Closure (Days)"){
        @Override
        public String getFullDescriptionByProcess(String process, String status) {
            return "Number of days after last reminder post which "+process+" Status should be converted to 'Declined'";
        }
    },
    EARLY_DEATH_CRITERIA("Early Death Criteria"){
        @Override
        public String getFullDescriptionByProcess(String process, String status) {
            return "Early Death Criteria";
        }
    };


    private String description;
    private String fullDescription;

    ProductLineProcessType(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return  description;
    }

    public abstract String getFullDescriptionByProcess(String process, String status);

}
