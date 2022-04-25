# Swedbank task implementation

##  How to test and launch the solution
* Build and launch app
```bash
git clone https://github.com/kostafey/swedbank-task.git
cd swedbank-task
./mvnw spring-boot:run
```
* The required database structure with test data (sample parking) will be created
* Some manual test cases can be found in `rest.http` file
* Integration autotests are in `SwedbankTestApplicationTests.java`

## REST API description

### Parking & pickup flow
1. `GET  /parking/request` - Request for available cell.
2. `POST /parking/park` - Actually park the car.
3. `POST /pickup/request` - Request for car pickup.
4. `POST /pickup/payAndTakeCar` - Actually pickup the car.

### Remaining endpoints
* `GET /floors` - List of floors and cells with with their states.
* `GET /activeOrders` - List all active orders.
* `POST /pickup/all` - Pickup all cars for debugging & in case of fire.