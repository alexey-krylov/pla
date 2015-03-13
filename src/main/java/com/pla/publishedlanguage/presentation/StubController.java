/*
 * Copyright (c) 3/11/15 8:37 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.publishedlanguage.presentation;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.nthdimenzion.security.service.UserLoginDetailDto;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

/**
 * @author: Samir
 * @since 1.0 11/03/2015
 */
@Controller
@RequestMapping(value = "/stub", produces = MediaType.APPLICATION_JSON_VALUE)
public class StubController {

    @RequestMapping(value = "/getuserdetail", method = RequestMethod.GET)
    @ResponseBody
    public UserLoginDetailDto getUserDetail() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        UserLoginDetailDto userLoginDetailDto = objectMapper.readValue(StubController.class.getResourceAsStream("/stubdata/userDetail.json"), UserLoginDetailDto.class);
        return userLoginDetailDto;
    }
}
