package com.kostafey.swedbanktest.controllers;

import java.math.BigDecimal;

import com.kostafey.swedbanktest.ParkingResporse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ParkingController {

    @GetMapping("/parking/request")
	public ParkingResporse greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
		return new ParkingResporse(
            1, 1,  new BigDecimal(10));
	}
}
