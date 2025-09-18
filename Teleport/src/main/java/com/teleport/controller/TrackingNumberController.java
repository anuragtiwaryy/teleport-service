package com.teleport.controller;


import com.teleport.dto.GenericResponseDto;
import com.teleport.dto.TrackingNumberRequest;
import com.teleport.dto.TrackingNumberResponse;
import com.teleport.service.TrackingNumberService;
import com.teleport.util.RateLimiterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class TrackingNumberController {

    @Autowired
    private TrackingNumberService trackingNumberService;

    @Autowired
    private RateLimiterService rateLimiterService;

    @GetMapping("/next-tracking-number")
    public ResponseEntity<?> getNextTrackingNumber(TrackingNumberRequest trackingNumberRequest){

        String customerId = trackingNumberRequest.getCustomer_id();
        int maxRequestsPerWindow = 100;
        int windowExpireInSeconds = 60;

        boolean allowed = rateLimiterService.isAllowed(customerId, maxRequestsPerWindow, windowExpireInSeconds);

        if (!allowed) {
            throw new RuntimeException("Rate limit exceeded for this Customer Id. Try again After Sometime.");
        }

        TrackingNumberResponse trackingNumberResponse = trackingNumberService.generateTrackingNumber(trackingNumberRequest);
        return new ResponseEntity<>(GenericResponseDto.success(trackingNumberResponse,"SUCCESS"), HttpStatus.OK);

    }

}
