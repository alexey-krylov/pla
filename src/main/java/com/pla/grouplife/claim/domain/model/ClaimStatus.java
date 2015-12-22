package com.pla.grouplife.claim.domain.model;

import lombok.Getter;


@Getter
public enum ClaimStatus {

INTIMATED("Claim Intimated"),EVALUATION("Claim Registered"),CANCELLED("Cancelled"),ROUTED("Routed to UnderWriter"),APPROVED("Approved"),REPUDIATED("Repudiated"),AWAITING("Awaiting Disbursement"),PAID("Paid");


    private String description;

    ClaimStatus(String description){
        this.description = description;
    }

    @Override
    public String toString() {
        return  description;
    }
 /*
    public static List<Map<String,Object>> getAllClaimStatus(){
        return Arrays.asList(ClaimStatus.values()).parallelStream().map(new Function<ClaimStatus, Map<String,Object>>() {
            @Override
            public Map<String, Object> apply(ClaimStatus claimStatus) {
                Map<String,Object> claimStatusMap = Maps.newLinkedHashMap();
                claimStatusMap.put("claimStatus",claimStatus.name());
                claimStatusMap.put("description",claimStatus.toString());
                return claimStatusMap;
            }
        }).collect(Collectors.toList());
    }
     */
}
