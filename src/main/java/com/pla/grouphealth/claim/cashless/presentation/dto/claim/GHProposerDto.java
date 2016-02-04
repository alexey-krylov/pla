package com.pla.grouphealth.claim.cashless.presentation.dto.claim;

import com.pla.grouphealth.sharedresource.model.vo.GHProposer;
import com.pla.grouphealth.sharedresource.model.vo.GHProposerContactDetail;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;

/**
 * Author - Mohan Sharma Created on 2/4/2016.
 */
@Getter
@Setter
@NoArgsConstructor
public class GHProposerDto {

    private String proposerName;
    private String proposerCode;
    private GHProposerContactDetail contactDetail;

    public GHProposerDto updateWithDetails(GHProposer ghProposer) {
        try {
            BeanUtils.copyProperties(this, ghProposer);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return this;
    }
}
