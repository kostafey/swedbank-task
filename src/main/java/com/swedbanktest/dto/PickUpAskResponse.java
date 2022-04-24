package com.swedbanktest.dto;

import java.math.BigDecimal;

public record PickUpAskResponse(
    Long orderId,
    Long minutes,
    BigDecimal price
) {}
