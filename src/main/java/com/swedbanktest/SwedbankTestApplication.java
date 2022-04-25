package com.swedbanktest;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import com.swedbanktest.db.Cell;
import com.swedbanktest.db.CellDAO;
import com.swedbanktest.db.Floor;
import com.swedbanktest.db.FloorDAO;
import com.swedbanktest.db.HibernateUtil;
import com.swedbanktest.db.Order;
import com.swedbanktest.db.OrderDAO;
import com.swedbanktest.dto.MeasuringResponse;
import com.swedbanktest.dto.ParkingResponse;
import com.swedbanktest.dto.PickUpAskResponse;
import com.swedbanktest.dto.PickUpResult;
import com.swedbanktest.dto.PickUpResult.ResultState;

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

	static BigDecimal getFloorAvailableWeight(Floor floor) {
		return floor.getWeightCapacity().subtract(
			getFloorUsedWeight(floor));
	}

	/**
	 * The first step for parking - request for available cell.
	 * @param weight Car weight in kilograms.
	 * @param height Car height in meters.
	 * @return Avalable place sign, floor, cell number and price per minute.
	 */
	public static MeasuringResponse processParkingRequest(
		BigDecimal weight, BigDecimal height) {
		Optional<Floor> selectedFloor = FloorDAO.list().stream()
			// Find all sutable floors
			.filter(f -> {
				return (
					// Floor height enough for this car
					f.getHeight().compareTo(height.add(HEIGHT_MARGIN)) > 0 &&
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
		if (scaleBigDecimal(floor.getHeight())
				.compareTo(scaleBigDecimal(new BigDecimal(3.20))) == 0) {
			return scaleBigDecimal(new BigDecimal(0.025));
		} else {
			return scaleBigDecimal(new BigDecimal(0.030));
		}
	}

	public static BigDecimal scaleBigDecimal(BigDecimal d) {
		return d.setScale(3, RoundingMode.HALF_UP);
	}

	/**
	 * Actually park the car. Assume it's a post paid.
	 * 
	 * @param cellId Cell id obtained from the first step
	 * @param floorNumber Floor number obtained from the first step
	 * @param weight Car weight in kilograms.
	 * @param height Car height in meters.
	 * @return Succesfull parked sign, order id, floor, cell number,
	 * date/time of parking and price per minute.
	 */
	public static ParkingResponse park(
		Integer cellId,
		Integer floorNumber,
		BigDecimal weight,
		BigDecimal height) {
		
		ParkingResponse cantParkResponse = new ParkingResponse(
			false, null,null, 
			null, null);
		if (cellId == null) {
			return cantParkResponse;
		}
		Session session = HibernateUtil.getSession();
		try {
			Transaction tx = session.beginTransaction();
			Cell cell = session.get(Cell.class, cellId);
			if (cell == null) {
				return cantParkResponse;
			}
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
		return cantParkResponse;
	}

	public static Long getDurationInMinutes(Order order) {
		return Math.max(1, TimeUnit.MILLISECONDS.toMinutes(
			order.getEnd().getTime() - order.getStart().getTime()));
	}

	/**
	 * Request for car pickup.
	 * 
	 * @param orderId Parking order id.
	 * @return Order id confirmation, parking duration in minutes and price to pay.
	 */
	public static PickUpAskResponse pickUpRequest(Long orderId) {
		return OrderDAO.get(orderId)
			.map(o -> {
				o.setEnd(new Date());
				OrderDAO.saveOrUpdate(o);
				Long minutes = getDurationInMinutes(o);
				BigDecimal price = getPricePerMinute(o.cell.floor)
					.multiply(new BigDecimal(minutes));
				return new PickUpAskResponse(orderId, minutes, price);
			})
			.orElse(new PickUpAskResponse(null, null,null));
	}

	/**
	 * Actually pickup the car.
	 * 
	 * @param orderId
	 * @param amountPaid
	 * @return
	 */
	public static PickUpResult payAndTakeCar(Long orderId, BigDecimal amountPaid) {
		return OrderDAO.get(orderId)
			.map(o -> {
				if (o.getEnd() == null) {
					return new PickUpResult(
						false, ResultState.PICK_UP_REQUEST_NOT_RECEIVED, o.getId());
				} else if (TimeUnit.MILLISECONDS.toMinutes(
					// The actual payment should be within 2 minutes from pickup
					// request. Otherwise the pickup request should be repeated.
					new Date().getTime() - o.getEnd().getTime()) > 120) {
						o.setEnd(null);
						OrderDAO.saveOrUpdate(o);
						return new PickUpResult(
							false, ResultState.PAYMENT_TIMEOUT, o.getId());
				}
				Cell cell = o.cell;
				if (cell.getOccupied() == false || o.getPaid() == true) {
					return new PickUpResult(
						false, ResultState.NO_CAR, o.getId());
				}
				BigDecimal price = getPricePerMinute(o.cell.floor)
					.multiply(new BigDecimal(getDurationInMinutes(o)));				
				if (amountPaid.compareTo(price) >= 0) {
					Session session = HibernateUtil.getSession();
					try {
						Transaction tx = session.beginTransaction();						
						if (cell.getOccupied() == true) {							
							cell.setOccupied(false);
							session.update(cell);
							o.setPaid(true);
							session.update(o);
							tx.commit();
							ResultState resultState = null;
							if (amountPaid.compareTo(price) == 0) {
								resultState = ResultState.OK;
							} else {
								resultState = ResultState.OVERPAID_CREDIT;
							}
							return new PickUpResult(true, resultState, o.getId());
						} else {
							tx.rollback();
						}	
					} catch (HibernateException e) {
						e.printStackTrace();
					} finally {
						HibernateUtil.closeSession();
					}
					return new PickUpResult(false, ResultState.ERROR, o.getId());
				} else {
					return new PickUpResult(false, ResultState.NOT_ENOUGH_MONEY, o.getId());
				}				
			})
			.orElse(new PickUpResult(false, ResultState.NO_SUCH_ORDER, orderId));
	}

	/**
	 * @return List of floors and cells with with their states.
	 */
	public static List<Floor> getFloors() {
		return FloorDAO.list();
	}

	/**
	 * @return List all active orders.
	 */	
	public static List<Order> getActiveOrders() {
		return OrderDAO.listActive();
	}	

	/**
	 * Pickup all cars for debugging & in case of fire.
	 * 
	 * @return List of floors and cells with with their states after.
	 */		
	public static List<Floor> pickupAll() {
		OrderDAO.listActive().forEach(o -> {
			o.setEnd(new Date());
			o.setPaid(true);
			OrderDAO.saveOrUpdate(o);
		});
		FloorDAO.list().forEach(f -> {
			f.cells.forEach(c -> {
				c.setOccupied(false);
				CellDAO.update(c);
			});
		});
		return FloorDAO.list();
	}

	public static void main(String[] args) {
		SpringApplication.run(SwedbankTestApplication.class, args);
	}
}
