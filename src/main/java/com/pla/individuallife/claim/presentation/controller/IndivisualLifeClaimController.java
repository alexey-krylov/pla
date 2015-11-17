package com.pla.individuallife.claim.presentation.controller;

import com.pla.core.query.MasterFinder;
import com.pla.grouplife.claim.application.command.GLClaimIntimationCommand;
import com.pla.grouplife.claim.application.service.GLClaimService;
import com.pla.sharedkernel.domain.model.ClaimType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

/**
 * Created by Mirror on 11/3/2015.
 */

@Controller
@RequestMapping(value = "/indivisuallife/claim")


public class IndivisualLifeClaimController {
        @Autowired
        private GLClaimService glClaimService;

        @Autowired
        private MasterFinder masterFinder;

        @RequestMapping(value = "/createclaimintimation", method = RequestMethod.POST)
        @ResponseBody
        public ResponseEntity createClaimIntimation(@RequestBody GLClaimIntimationCommand glClaimIntimationCommand, BindingResult bindingResult) {
            return null;
        }
        @RequestMapping(value = "/openclaimintimationpage", method = RequestMethod.GET)
        public ModelAndView claimIntimation() {
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("pla/individuallife/claim/claimIntimation");
            return modelAndView;
        }
        @RequestMapping(value = "/openclaimsettlementpage", method = RequestMethod.GET)
        public ModelAndView claimSettlement() {
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("pla/individuallife/claim/claimSettlement");
            return modelAndView;
        }
        @RequestMapping(value = "/openencashmentclaimpage", method = RequestMethod.GET)
        public ModelAndView encashmentClaim() {
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("pla/individuallife/claim/encashmentClaim");
            return modelAndView;
        }
        @RequestMapping(value = "/opensurrenderclaimpage", method = RequestMethod.GET)
        public ModelAndView surrenderClaim() {
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("pla/individuallife/claim/surrenderclaim");
            return modelAndView;
        }
        @RequestMapping(value = "/openmaturityclaimpage", method = RequestMethod.GET)
        public ModelAndView maturityClaim() {
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("pla/individuallife/claim/maturityClaim");
            return modelAndView;
        }
        @RequestMapping(value = "/opendisabilityclaimpage", method = RequestMethod.GET)
        public ModelAndView disabilityClaim() {
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("pla/individuallife/claim/disabilityClaim");
            return modelAndView;
        }
        @RequestMapping(value = "/opendeathclaimpage", method = RequestMethod.GET)
        public ModelAndView deathClaim() {
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("pla/individuallife/claim/deathClaim");
            return modelAndView;
        }

        @RequestMapping(value = "/openclaimsearchpage", method = RequestMethod.GET)
        public ModelAndView searchPolicy() {
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("pla/individuallife/claim/claimSearch");
            return modelAndView;
        }


        @RequestMapping(value = "/opennonfuneralproudctpage", method = RequestMethod.GET)
        public ModelAndView nonfuneralclaim() {
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("pla/individuallife/claim/nonFuneralProudct");
            return modelAndView;
        }
      @RequestMapping(value = "/openfuneralproudctpage", method = RequestMethod.GET)
       public ModelAndView funeralclaim() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/individuallife/claim/funeralProudct");
        return modelAndView;
      }

//        @RequestMapping(value = "/assuredsearch",method = RequestMethod.POST)
//        @ResponseBody
//        public List<GLInsuredDetailDto> assuredSearch(@RequestBody AssuredSearchDto assuredSearchDto){
//            return glClaimService.assuredSearch(assuredSearchDto);
//        }


//        @RequestMapping(value = "/claimintimationdetail/{policyNumber}",method = RequestMethod.GET)
//        @ResponseBody
//        public List<ClaimIntimationDetailDto> getClaimIntimationDetail(@PathVariable("policyNumber")String policyNumber){
//            return glClaimService.getClaimIntimationDetail(policyNumber);
//        }


        @RequestMapping(value = "/getclaimtype",method = RequestMethod.GET)
        @ResponseBody
        public List<Map<String, Object>> getClaimType(){
            return ClaimType.getAllClaimType();
        }

        @RequestMapping(method = RequestMethod.GET, value = "/getAllBankNames")
        @ResponseBody
        public List<Map<String, Object>> getAllBankNames() {
            return masterFinder.getAllBank();
        }

        @RequestMapping(method = RequestMethod.GET, value = "/getAllBankBranchNames/{bankCode}")
        @ResponseBody
        public List<Map<String, Object>> getAllBankBranchNames(@PathVariable("bankCode") String bankCode) {
            return masterFinder.getAllBankBranch(bankCode);
        }


    }