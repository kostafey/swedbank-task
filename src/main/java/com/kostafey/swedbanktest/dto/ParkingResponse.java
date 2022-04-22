package com.kostafey.swedbanktest.dto;

import java.util.Date;

public record ParkingResponse(
	boolean parked,
	Integer floor,
	Integer cell,
	Long orderId,
	Date time) {}
