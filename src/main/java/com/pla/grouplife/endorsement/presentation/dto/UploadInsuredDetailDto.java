package com.pla.grouplife.endorsement.presentation.dto;

import com.pla.grouplife.sharedresource.model.GLEndorsementType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by Samir on 9/6/2015.
 */
@Getter
@Setter
public class UploadInsuredDetailDto {

    private MultipartFile file;

    private String endorsementId;

    private GLEndorsementType endorsementType;


}
