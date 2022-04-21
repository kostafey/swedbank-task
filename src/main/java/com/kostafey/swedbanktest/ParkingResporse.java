package com.kostafey.swedbanktest;

import java.math.BigDecimal;

public record ParkingResporse(
	int floor,
	int cell,
	BigDecimal price
) {};
