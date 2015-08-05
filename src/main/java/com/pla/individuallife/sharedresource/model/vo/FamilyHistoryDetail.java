package com.pla.individuallife.sharedresource.model.vo;

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
    private Question closeRelative;
}
