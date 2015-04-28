package com.pla.core.dto;

import com.pla.publishedlanguage.domain.model.PremiumFrequency;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by Admin on 4/28/2015.
 */
@Getter
@Setter
public class PremiumFrequencyFollowUpDto {
    PremiumFrequency premiumFrequency;
    List<ProductLineProcessItemDto> premiumFollowUpFrequencyItems;
}
