package com.pla.individuallife.proposal.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by Prasant on 17-Jun-15.
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class FamilyHistoryDetail {
    private ParentDetail father;
    private ParentDetail mother;
    private SiblingsDetail brother;
    private SiblingsDetail sister;
    private Question question_16;
}
