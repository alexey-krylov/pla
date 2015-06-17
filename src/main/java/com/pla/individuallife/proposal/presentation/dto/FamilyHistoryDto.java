package com.pla.individuallife.proposal.presentation.dto;

import com.pla.individuallife.proposal.domain.model.ParentDetail;
import com.pla.individuallife.proposal.domain.model.Question;
import com.pla.individuallife.proposal.domain.model.SiblingsDetail;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Prasant on 12-Jun-15.
 */
@Getter
@Setter
@NoArgsConstructor
public class FamilyHistoryDto {

    private ParentDetail father;
    private ParentDetail mother;
    private SiblingsDetail brother;
    private SiblingsDetail sister;
    private Question question_16;
}
