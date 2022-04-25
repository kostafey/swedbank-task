package com.swedbanktest.controllers;

import java.math.BigDecimal;
import java.util.List;

import com.swedbanktest.SwedbankTestApplication;
import com.swedbanktest.db.Floor;
import com.swedbanktest.db.Order;
import com.swedbanktest.dto.MeasuringResponse;
import com.swedbanktest.dto.ParkingResponse;
import com.swedbanktest.dto.PickUpAskResponse;
import com.swedbanktest.dto.PickUpResult;

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

	@PostMapping("/pickup/request")
	public PickUpAskResponse pickUpRequest(
		@RequestParam(value = "orderId") Long orderId) {
		return SwedbankTestApplication.pickUpRequest(orderId);
	}

	@PostMapping("/pickup/payAndTakeCar")
	public PickUpResult payAndTakeCar(
		@RequestParam(value = "orderId") Long orderId,
		@RequestParam(value = "amountPaid") BigDecimal amountPaid) {
		return SwedbankTestApplication.payAndTakeCar(orderId, amountPaid);
	}

	@GetMapping("/floors")
	public List<Floor> getFloors() {
		return SwedbankTestApplication.getFloors();
	}

	@GetMapping("/activeOrders")
	public List<Order> getOrders() {
		return SwedbankTestApplication.getActiveOrders();
	}

	/**
	 * For debugging & in case of fire.
	 */
	@PostMapping("/pickup/all")
	public List<Floor> pickupAll() {
		return SwedbankTestApplication.pickupAll();
	}
}
