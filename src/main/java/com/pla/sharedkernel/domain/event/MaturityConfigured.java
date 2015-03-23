package com.pla.sharedkernel.domain.event;

import com.pla.sharedkernel.domain.model.MaturityAmount;
import lombok.Getter;

import java.util.Set;

/**
 * @author: pradyumna
 * @since 1.0 21/03/2015
 */
@Getter
public class MaturityConfigured {
    private Set<MaturityAmount> maturityAmounts;

    public MaturityConfigured(Set<MaturityAmount> maturityAmounts) {
        this.maturityAmounts = maturityAmounts;

    }
}
