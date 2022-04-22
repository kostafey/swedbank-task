package com.kostafey.swedbanktest.controllers;

import java.math.BigDecimal;

import com.kostafey.swedbanktest.SwedbankTestApplication;
import com.kostafey.swedbanktest.dto.MeasuringResponse;
import com.kostafey.swedbanktest.dto.ParkingResponse;
import com.kostafey.swedbanktest.dto.PickUpRequest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ParkingController {

    @GetMapping("/parking/request")
	public MeasuringResponse parkingRequest(
		@RequestParam(value = "weight") BigDecimal weight,
		@RequestParam(value = "height") BigDecimal height) {
		return SwedbankTestApplication.processParkingRequest(weight, height);
	}

	@PostMapping("/parking/park")
	public ParkingResponse park(
		@RequestParam(value = "cell") Integer cell,
		@RequestParam(value = "floor") Integer floor,
		@RequestParam(value = "weight") BigDecimal weight,
		@RequestParam(value = "height") BigDecimal height) {
		return SwedbankTestApplication.park(cell, floor, weight, height);
	}

	@GetMapping("/pickup/request")
	public PickUpRequest pickUpRequest(
		@RequestParam(value = "orderId") Long orderId) {
		return SwedbankTestApplication.pickUpRequest(orderId);
	}

	@PostMapping("/pickup/payAndTakeAway")
	public PickUpRequest payAndTakeAway(
		@RequestParam(value = "orderId") Long orderId,
		@RequestParam(value = "amountPaid") BigDecimal amountPaid) {
		return SwedbankTestApplication.payAndTakeAway(orderId, amountPaid);
	}
}
