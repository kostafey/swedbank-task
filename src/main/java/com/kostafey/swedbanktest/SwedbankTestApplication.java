package com.kostafey.swedbanktest;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import com.kostafey.swedbanktest.db.Cell;
import com.kostafey.swedbanktest.db.Floor;
import com.kostafey.swedbanktest.db.FloorDAO;
import com.kostafey.swedbanktest.db.HibernateUtil;
import com.kostafey.swedbanktest.db.Order;
import com.kostafey.swedbanktest.db.OrderDAO;
import com.kostafey.swedbanktest.dto.MeasuringResponse;
import com.kostafey.swedbanktest.dto.ParkingResponse;
import com.kostafey.swedbanktest.dto.PickUpRequest;
import com.kostafey.swedbanktest.dto.PickUpResult;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

@SpringBootApplication
public class SwedbankTestApplication {

	private static final BigDecimal HEIGHT_MARGIN = new BigDecimal(0.1);

	private static BigDecimal getFloorUsedWeight(Floor floor) {
		return floor.cells.stream()
			.map(c -> c.getWeightUsed())
			.reduce(new BigDecimal(0), (c1, c2) -> c1.add(c2));
	}

	private static BigDecimal getFloorAvailableWeight(Floor floor) {
		return floor.getWeightCapacity().subtract(
			getFloorUsedWeight(floor));
	}

	public static MeasuringResponse processParkingRequest(
		BigDecimal weight, BigDecimal height) {
		Optional<Floor> selectedFloor = FloorDAO.list().stream()
			// Find all sutable floors
			.filter(f -> {			
				return (
					// Floor height enough for this car
					f.getHeight().add(HEIGHT_MARGIN).compareTo(height) > 0 &&
					// Any free cell exists
					f.cells.stream().anyMatch(c -> c.getOccupied() == false) &&
					// Total floor available weight not exceeded
					getFloorAvailableWeight(f).compareTo(weight) > 0);})
			// Find lowest/cheapest floor
			.reduce((f1, f2) -> getPricePerMinute(f1)
				.compareTo(getPricePerMinute(f2)) <= 0 ? f1 : f2);

		return selectedFloor
			.map(f -> {
				Integer cellId = f.cells.stream()
					.filter(c -> c.getOccupied() == false)
					.findAny().get()
					.getId();
				return new MeasuringResponse(
					true, f.getFloorNumber(), cellId, getPricePerMinute(f));})
			.orElse(new MeasuringResponse(
				false, 0, 0,  new BigDecimal(0)));
	}

	public static BigDecimal getPricePerMinute(Floor floor) {
		if (floor.getHeight() == new BigDecimal(3.2)) {
			return new BigDecimal(0.010);
		} else {
			return new BigDecimal(0.012);
		}
	}

	public static ParkingResponse park(
		Integer cellId,
		Integer floorNumber,
		BigDecimal weight,
		BigDecimal height) {

		Session session = HibernateUtil.getSession();
		try {
			Transaction tx = session.beginTransaction();
			Cell cell = session.get(Cell.class, cellId);
			Order order = new Order(cell);
			Long orderId = null;
			if (cell.getOccupied() == false && 
				getFloorAvailableWeight(cell.floor).compareTo(weight) > 0) {
				orderId = (Long)session.save(order);
				cell.setOccupied(true);
				session.update(cell);
				tx.commit();
				return new ParkingResponse(
					true, cell.floor.getFloorNumber(),
					cell.getId(), orderId, order.getStart());
			} else {
				tx.rollback();
			}	
		} catch (HibernateException e) {
			e.printStackTrace();
		} finally {
			HibernateUtil.closeSession();
		}
		return new ParkingResponse(
			false, null,null, 
			null, null);
	}

	public static Long getDurationInMinutes(Order order) {
		return TimeUnit.MILLISECONDS.toMinutes(
			order.getStart().getTime() - order.getEnd().getTime());
	}

	public static PickUpRequest pickUpRequest(Long orderId) {
		return OrderDAO.get(orderId)
			.map(o -> {
				Long minutes = getDurationInMinutes(o);
				BigDecimal price = getPricePerMinute(o.cell.floor)
					.multiply(new BigDecimal(minutes));
				return new PickUpRequest(orderId, minutes, price);
			})
			.orElse(new PickUpRequest(null, null,null));
	}

	public static PickUpResult payAndTakeCar(Long orderId, BigDecimal amountPaid) {
		return OrderDAO.get(orderId)
			.map(o -> {
				BigDecimal price = getPricePerMinute(o.cell.floor)
					.multiply(new BigDecimal(getDurationInMinutes(o)));
				// The actual payment should be within the same minute
				if (amountPaid == price) {
					Session session = HibernateUtil.getSession();
					try {
						Transaction tx = session.beginTransaction();
						Cell cell = o.cell;
						if (cell.getOccupied() == true) {							
							cell.setOccupied(true);
							session.update(cell);
							o.setPaid(true);
							session.update(o);
							tx.commit();
							return new PickUpResult(true, orderId);
						} else {
							tx.rollback();
						}	
					} catch (HibernateException e) {
						e.printStackTrace();
					} finally {
						HibernateUtil.closeSession();
					}
				}
				return new PickUpResult(false, orderId);
			})
			.orElse(new PickUpResult(false, null));
	}

	public static void main(String[] args) {
		SpringApplication.run(SwedbankTestApplication.class, args);
	}
}
