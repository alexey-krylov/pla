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
public class BuildDetail {
    private float height;
    private String heightType;
    private double weight;
    private String weightType;
    private Question overWeightQuestion;
}
