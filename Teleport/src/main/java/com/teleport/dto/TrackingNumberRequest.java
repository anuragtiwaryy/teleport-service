package com.teleport.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class TrackingNumberRequest {
    @NotBlank(message = "Origin country ID is required")
    @Size(min = 2, max = 2, message = "Origin country ID must be exactly 2 characters")
    private String origin_country_id;

    @NotBlank(message = "Destination country ID is required")
    @Size(min = 2, max = 2, message = "Destination country ID must be exactly 2 characters")
    private String destination_country_id;

    @NotNull(message = "Weight is required")
    @DecimalMin(value = "0.1", inclusive = true, message = "Weight must be greater than 0")
    private BigDecimal weight;

    @NotBlank(message = "Created at date is required")
    @Pattern(
            regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\+\\d{2}:\\d{2}$",
            message = "Created at must be in format yyyy-MM-ddTHH:mm:ss+HH:mm"
    )
    private String created_at;

    @NotBlank(message = "Customer ID is required")
    @Size(max = 30, message = "Customer ID cannot exceed 30 characters")
    private String customer_id;

    @NotBlank(message = "Customer name is required")
    @Size(max = 30, message = "Customer name cannot exceed 30 characters")
    private String customer_name;

    @NotBlank(message = "Customer slug is required")
    @Size(max = 30, message = "Customer slug cannot exceed 30 characters")
    private String customer_slug;
}