package com.teleport.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.OffsetDateTime;

@Setter
@Getter
@Builder
@ToString
public class TrackingNumberResponse {

    private String trackingNumber;
    private OffsetDateTime createdAt;
}
