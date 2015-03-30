package com.pla.core.application;

import lombok.*;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Set;

/**
 * Created by Admin on 3/27/2015.
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
@AllArgsConstructor
public class UpdateMandatoryDocumentCommand {

    private UserDetails userDetails;

    private Long id;

    private Set<String> documents;
}
