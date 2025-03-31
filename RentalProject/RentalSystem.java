import java.util.List;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

public class RentalSystem {
	private static RentalSystem instance;
    private List<Vehicle> vehicles = new ArrayList<>();
    private List<Customer> customers = new ArrayList<>();
    private RentalHistory rentalHistory = new RentalHistory();

    private RentalSystem() {
    	loadData();
    }
    
    
    private void loadData() {
        // Load vehicles from vehicles.ser
        try (ObjectInputStream vehicleReader = new ObjectInputStream(new FileInputStream("vehicles.ser"))) {
            Vehicle vehicle;
            while (true) {  // Keep reading until end of file
                try {
                    vehicle = (Vehicle) vehicleReader.readObject();
                    vehicles.add(vehicle);  // Add each vehicle to the list
                } catch (EOFException e) {
                    break;  // End of file reached
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("No vehicles data found, starting fresh.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("An error occurred while loading vehicles: " + e.getMessage());
        }

        // Load customers from customers.ser
        try (ObjectInputStream customerReader = new ObjectInputStream(new FileInputStream("customers.ser"))) {
            Customer customer;
            while (true) {  // Keep reading until end of file
                try {
                    customer = (Customer) customerReader.readObject();
                    customers.add(customer);  // Add each customer to the list
                } catch (EOFException e) {
                    break;  // End of file reached
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("No customers data found, starting fresh.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("An error occurred while loading customers: " + e.getMessage());
        }

        // Load rental records from rental_records.ser
        try (ObjectInputStream recordReader = new ObjectInputStream(new FileInputStream("rental_records.ser"))) {
            RentalRecord record;
            while (true) {  // Keep reading until end of file
                try {
                    record = (RentalRecord) recordReader.readObject();
                    rentalHistory.addRecord(record);  // Add each rental record to the list
                } catch (EOFException e) {
                    break;  // End of file reached
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("No rental records data found, starting fresh.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("An error occurred while loading rental records: " + e.getMessage());
        }
    }


    
    
    public static RentalSystem getInstance() {
    	if (instance == null) {
    		instance = new RentalSystem();
    	}
    	return instance;
    }
    
    public void addVehicle(Vehicle vehicle) {
        vehicles.add(vehicle);
        saveVehicle(vehicle);
    }

    public void addCustomer(Customer customer) {
        customers.add(customer);
        saveCustomer(customer);
    }

    public void rentVehicle(Vehicle vehicle, Customer customer, LocalDate date, double amount) {
        if (vehicle.getStatus() == Vehicle.VehicleStatus.AVAILABLE) {
            vehicle.setStatus(Vehicle.VehicleStatus.RENTED);
            RentalRecord record = new RentalRecord(vehicle, customer, date, amount, "RENT");
            rentalHistory.addRecord(record);
            saveRecord(record);
            System.out.println("Vehicle rented to " + customer.getCustomerName());
        }
        else {
            System.out.println("Vehicle is not available for renting.");
        }
    }

    public void returnVehicle(Vehicle vehicle, Customer customer, LocalDate date, double extraFees) {
        if (vehicle.getStatus() == Vehicle.VehicleStatus.RENTED) {
            vehicle.setStatus(Vehicle.VehicleStatus.AVAILABLE);
            RentalRecord record = new RentalRecord(vehicle, customer, date, extraFees, "RETURN");
            rentalHistory.addRecord(record);
            saveRecord(record);
            System.out.println("Vehicle returned by " + customer.getCustomerName());
        }
        else {
            System.out.println("Vehicle is not rented.");
        }
    }    

    public void displayVehicles(boolean onlyAvailable) {
    	System.out.println("|     Type         |\tPlate\t|\tMake\t|\tModel\t|\tYear\t|");
    	System.out.println("---------------------------------------------------------------------------------");
    	 
        for (Vehicle v : vehicles) {
            if (!onlyAvailable || v.getStatus() == Vehicle.VehicleStatus.AVAILABLE) {
                System.out.println("|     " + (v instanceof Car ? "Car          " : "Motorcycle   ") + "|\t" + v.getLicensePlate() + "\t|\t" + v.getMake() + "\t|\t" + v.getModel() + "\t|\t" + v.getYear() + "\t|\t");
            }
        }
        System.out.println();
    }
    
    public void displayAllCustomers() {
        for (Customer c : customers) {
            System.out.println("  " + c.toString());
        }
    }
    
    public void displayRentalHistory() {
        for (RentalRecord record : rentalHistory.getRentalHistory()) {
            System.out.println(record.toString());
        }
    }
    
    public Vehicle findVehicleByPlate(String plate) {
        for (Vehicle v : vehicles) {
            if (v.getLicensePlate().equalsIgnoreCase(plate)) {
                return v;
            }
        }
        return null;
    }
    
    public Customer findCustomerById(String id) {
        for (Customer c : customers)
            if (c.getCustomerId() == Integer.parseInt(id))
                return c;
        return null;
    }
    
    public void saveVehicle(Vehicle vehicle) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("vehicles.ser", true))) {
            out.writeObject(vehicle);
        } catch (IOException e) {
            System.out.println("An error occurred while saving the vehicle: " + e.getMessage());
        }
    }

    public void saveCustomer(Customer customer) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("customers.ser", true))) {
            out.writeObject(customer);
        } catch (IOException e) {
            System.out.println("An error occurred while saving the customer: " + e.getMessage());
        }
    }

    public void saveRecord(RentalRecord record) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("rental_records.ser", true))) {
            out.writeObject(record);
        } catch (IOException e) {
            System.out.println("An error occurred while saving the rental record: " + e.getMessage());
        }
    }
}