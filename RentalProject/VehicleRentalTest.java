import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

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
}
