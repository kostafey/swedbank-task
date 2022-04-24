package com.swedbanktest.dto;

import java.math.BigDecimal;

public record MeasuringResponse(
    boolean canBeParked,
    int floor,
    int cell,
    BigDecimal pricePerMinute) {}
