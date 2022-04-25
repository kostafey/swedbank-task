package com.swedbanktest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.swedbanktest.db.Cell;
import com.swedbanktest.db.Floor;
import com.swedbanktest.db.FloorDAO;
import com.swedbanktest.dto.MeasuringResponse;
import com.swedbanktest.dto.ParkingResponse;
import com.swedbanktest.dto.PickUpAskResponse;
import com.swedbanktest.dto.PickUpResult;
import com.swedbanktest.dto.PickUpResult.ResultState;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SwedbankTestApplicationTests {

	@Test
	void getFloorAvailableWeightTest() {
		Floor testFloor = new Floor(1, -3, new BigDecimal(3.2), new BigDecimal(10000), 
                Arrays.asList(
                    new Cell(1, 1, new BigDecimal(1000), false),
                    new Cell(2, 1, new BigDecimal(2000), false),
                    new Cell(3, 1, new BigDecimal(3000), false)));

		assertEquals(
			new BigDecimal(4000),
			SwedbankTestApplication.getFloorAvailableWeight(testFloor));
	}

	@Test
	void integrationTestBasic() {

		BigDecimal weight = new BigDecimal(975);
		BigDecimal height = new BigDecimal(1.2);
		BigDecimal paymentAmount = new BigDecimal(0.025)
			.setScale(3, RoundingMode.HALF_UP);

		MeasuringResponse measuringResponse = SwedbankTestApplication.processParkingRequest(
			weight, height);
		assertEquals(
			new MeasuringResponse(
				true, -3, 1, paymentAmount), 
			measuringResponse);
		
		ParkingResponse parkingResponse = SwedbankTestApplication.park(
			measuringResponse.cell(),
			measuringResponse.floor(),
			weight,
			height);
		assertEquals(true, parkingResponse.parked());
		assertEquals(-3, parkingResponse.floor());
		assertEquals(1, parkingResponse.cell());	
		
		PickUpAskResponse pickUpAskResponse = SwedbankTestApplication.pickUpRequest(
			parkingResponse.orderId());
		assertEquals(1, pickUpAskResponse.minutes());
		assertEquals(paymentAmount, pickUpAskResponse.price());

		PickUpResult payAndTakeCar = SwedbankTestApplication.payAndTakeCar(
			pickUpAskResponse.orderId(), pickUpAskResponse.price());
		assertEquals(true, payAndTakeCar.carReturned());
		assertEquals(ResultState.OK, payAndTakeCar.resultState());
	}

	@Test
	void integrationTestBigCar() {

		BigDecimal weight = new BigDecimal(975);
		BigDecimal height = new BigDecimal(3.3);
		BigDecimal paymentAmount = new BigDecimal(0.030)
			.setScale(3, RoundingMode.HALF_UP);

		MeasuringResponse measuringResponse = SwedbankTestApplication.processParkingRequest(
			weight, height);
		assertEquals(
			new MeasuringResponse(
				true, -1, 17, paymentAmount), 
			measuringResponse);
		
		ParkingResponse parkingResponse = SwedbankTestApplication.park(
			measuringResponse.cell(),
			measuringResponse.floor(),
			weight,
			height);
		assertEquals(true, parkingResponse.parked());
		assertEquals(-1, parkingResponse.floor());
		assertEquals(17, parkingResponse.cell());	
		
		PickUpAskResponse pickUpAskResponse = SwedbankTestApplication.pickUpRequest(
			parkingResponse.orderId());
		assertEquals(1, pickUpAskResponse.minutes());
		assertEquals(paymentAmount, pickUpAskResponse.price());

		PickUpResult payAndTakeCar = SwedbankTestApplication.payAndTakeCar(
			pickUpAskResponse.orderId(), pickUpAskResponse.price());
		assertEquals(true, payAndTakeCar.carReturned());
		assertEquals(ResultState.OK, payAndTakeCar.resultState());
	}	

	@Test
	void integrationTestCarsOverflow() {

		BigDecimal weight = new BigDecimal(975);
		BigDecimal height = new BigDecimal(1.2);
		Integer cellsCount = FloorDAO.list().stream()
			.map(f -> f.cells.size())
			.collect(Collectors.summingInt(Integer::intValue));
		IntStream.range(0, cellsCount).forEach(i -> {
			MeasuringResponse measuringResponse = SwedbankTestApplication.processParkingRequest(
				weight, height);
			ParkingResponse parkingResponse = SwedbankTestApplication.park(
				measuringResponse.cell(),
				measuringResponse.floor(),
				weight,
				height);
			assertEquals(true, parkingResponse.parked());
		});
				
		MeasuringResponse measuringResponse = SwedbankTestApplication.processParkingRequest(
			weight, height);
		ParkingResponse parkingResponse = SwedbankTestApplication.park(
			measuringResponse.cell(),
			measuringResponse.floor(),
			weight,
			height);
		assertEquals(false, parkingResponse.parked());
	}

	@AfterEach
    void cleanup() {
        SwedbankTestApplication.pickupAll();
    }   

}
