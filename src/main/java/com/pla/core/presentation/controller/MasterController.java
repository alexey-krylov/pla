/*
 * Copyright (c) 3/20/15 9:32 AM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.presentation.controller;

import com.pla.core.dto.GeoDto;
import com.pla.core.query.MasterFinder;
import com.pla.sharedkernel.domain.model.GeoType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * @author: Samir
 * @since 1.0 20/03/2015
 */
@Controller
@RequestMapping(value = "/core/master", consumes = MediaType.ALL_VALUE)
public class MasterController {

    private MasterFinder masterFinder;

    @Autowired
    public MasterController(MasterFinder masterFinder) {
        this.masterFinder = masterFinder;
    }


    @RequestMapping(value = "/getchannelType", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Map<String, Object>> getAllChannelType() {
        return masterFinder.getAllChanelType();
    }

    @RequestMapping(value = "/getgeodetail", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<GeoDto> getGeoDetail() {
        List<Map<String, Object>> provinces = masterFinder.getGeoByGeoType(GeoType.PROVINCE);
        List<Map<String, Object>> cities = masterFinder.getGeoByGeoType(GeoType.CITY);
        List<GeoDto> geoDtoList = GeoDto.transformToGeoDto(provinces, cities);
        return geoDtoList;
    }

    @RequestMapping(value = "/getbranchbyregion", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Map<String, Object>> getBranchByRegion(@RequestParam(value = "regioncode", required = false) String regionCode) {
        return masterFinder.getBranchByRegion(regionCode);
    }

}
