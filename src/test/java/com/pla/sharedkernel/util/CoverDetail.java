package com.pla.sharedkernel.util;

/**
 * Created by User on 4/22/2015.
 */
public class CoverDetail {

    private String category;
    private String relationship;
    private String planCoverageName;
    private String planCoverageSumAssured;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public String getPlanCoverageSumAssured() {
        return planCoverageSumAssured;
    }

    public void setPlanCoverageSumAssured(String planCoverageSumAssured) {
        this.planCoverageSumAssured = planCoverageSumAssured;
    }

    public String getPlanCoverageName() {
        return planCoverageName;

    }

    public void setPlanCoverageName(String PlanName) {
        this.planCoverageName = PlanName;
    }
}
