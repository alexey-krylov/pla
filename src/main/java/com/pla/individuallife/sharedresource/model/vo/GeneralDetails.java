package com.pla.individuallife.sharedresource.model.vo;

import com.pla.individuallife.sharedresource.dto.QuestionAnswerDto;
import lombok.*;

import java.util.List;

/**
 * Created by Karunakar on 7/6/2015.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class GeneralDetails {

    private GeneralDetailQuestion assuredByPLAL;
    private GeneralDetailQuestion assuredByOthers;
    private GeneralDetailQuestion pendingInsuranceByOthers;
    private GeneralDetailQuestion assuranceDeclined;
    private List<QuestionAnswerDto> questionAndAnswers;

}
