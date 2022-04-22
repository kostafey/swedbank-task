package com.kostafey.swedbanktest.dto;

import java.math.BigDecimal;

public record PickUpRequest(
    Long orderId,
    Long minutes,
    BigDecimal price    
) {}
