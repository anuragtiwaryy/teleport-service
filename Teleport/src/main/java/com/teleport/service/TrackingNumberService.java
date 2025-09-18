package com.teleport.service;

import com.teleport.dto.TrackingNumberRequest;
import com.teleport.dto.TrackingNumberResponse;

public interface TrackingNumberService {

    TrackingNumberResponse generateTrackingNumber(
            TrackingNumberRequest trackingNumberRequest);
}
