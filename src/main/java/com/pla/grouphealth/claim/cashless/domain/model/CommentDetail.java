package com.pla.grouphealth.claim.cashless.domain.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

import static org.springframework.util.Assert.*;

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
