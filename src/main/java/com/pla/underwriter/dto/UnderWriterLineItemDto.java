package com.pla.underwriter.dto;

import com.pla.underwriter.domain.model.UnderWriterInfluencingFactor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Admin on 5/8/2015.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UnderWriterLineItemDto {
    private UnderWriterInfluencingFactor underWriterInfluencingFactor;
    private String influencingItemFrom;
    private String influencingItemTo;

}
