import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;


class VehicleRentalTest {

	@Test
	public void testLicensePlateValidation() {
		Vehicle testV = new Car("Toyota", "Corolla", 2019, 6);
		
		testV.setLicensePlate("AAA100");
		assertTrue("AAA100".equals(testV.getLicensePlate()));
		testV.setLicensePlate("ABC567");
		assertTrue("ABC567".equals(testV.getLicensePlate()));
		testV.setLicensePlate("ZZZ999");
		assertTrue("ZZZ999".equals(testV.getLicensePlate()));
		
		assertThrows(IllegalArgumentException.class, () -> testV.setLicensePlate(""));
		assertThrows(IllegalArgumentException.class, () -> testV.setLicensePlate(null));
		assertThrows(IllegalArgumentException.class, () -> testV.setLicensePlate("AAA1000"));
		assertThrows(IllegalArgumentException.class, () -> testV.setLicensePlate("ZZZ99"));
	}
	
	@Test
	public void testRentAndReturnVehicle() {
		Vehicle testV = new Truck("Dodge", "Charger", 2016, 5);
		Customer testC = new Customer(12345, "Bob");
		testV.setLicensePlate("ABC123");
		assertTrue("ABC123".equals(testV.getLicensePlate()));
		assertTrue(testV.getStatus().equals(Vehicle.VehicleStatus.AVAILABLE));
		
		RentalSystem rentSys = RentalSystem.getInstance();
		
		boolean rentResult = rentSys.rentVehicle(testV, testC, LocalDate.now(), 100);
		assertTrue(rentResult == true);
		assertTrue(testV.getStatus().equals(Vehicle.VehicleStatus.RENTED));
		rentResult = rentSys.rentVehicle(testV, testC, LocalDate.now(), 100);
		assertTrue(rentResult == false);
		
		boolean returnResult = rentSys.returnVehicle(testV, testC, LocalDate.now(), 50);
		assertTrue(returnResult == true);
		assertTrue(testV.getStatus().equals(Vehicle.VehicleStatus.AVAILABLE));
		returnResult = rentSys.returnVehicle(testV, testC, LocalDate.now(), 50);
		assertTrue(returnResult == false);
	}
	
	@Test
	public void testSingletonRentalSystem() throws Exception {
		 Constructor<RentalSystem> constructor  =  RentalSystem.class.getDeclaredConstructor();
		 int modResult = constructor.getModifiers();
		 assertTrue(modResult == (Modifier.PRIVATE));
		 RentalSystem rentSys = RentalSystem.getInstance();
		 assertFalse(rentSys == null);
	}
}
