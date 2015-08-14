package com.pla.individuallife.identifier;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * Created by Prasant on 12-Jun-15.
 */
@EqualsAndHashCode(of = "questionId")
@Embeddable
@NoArgsConstructor
@Getter
@JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
public class QuestionId implements Serializable {
    private String questionId;

    public QuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String toString() {
        return this.questionId;
    }
}
