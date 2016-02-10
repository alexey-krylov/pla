package com.pla.grouphealth.claim.cashless.domain.model.sharedmodel;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;

/**
 * Created by Mohan Sharma on 1/18/2016.
 */
@NoArgsConstructor
@Getter
@EqualsAndHashCode
public class CommentDetail {
    private String comments;
    private DateTime commentDateTime;
    private String userName;

    public CommentDetail updateWithComments(String comments) {
        this.comments = comments;
        return this;
    }

    public CommentDetail updateWithCommentDateTime(DateTime commentDateTime) {
        this.commentDateTime = commentDateTime;
        return this;
    }

    public CommentDetail updateWithUserName(String username) {
        this.userName = username;
        return this;
    }
}