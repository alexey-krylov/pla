package com.pla.individuallife.sharedresource.model.vo;

import lombok.*;

/**
 * Created by Karunakar on 7/1/2015.
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
@AllArgsConstructor
public class AdditionalDetails {

    private String medicalAttendantDetails;
    private String medicalAttendantDuration;
    private String dateAndReason;
    private ReplacementQuestion replacementDetails;
}
