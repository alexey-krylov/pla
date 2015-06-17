package com.pla.individuallife.proposal.presentation.dto;

import com.pla.individuallife.proposal.domain.model.Question;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by ASUS on 12-Jun-15.
 */
@Getter
@Setter
@NoArgsConstructor
public class BuildDto {
    private float height;
    private String heightType;
    private double weight;
    private String weightType;
    private Question question_21;
}
