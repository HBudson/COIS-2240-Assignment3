import java.util.List;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

public class RentalSystem {
	private static RentalSystem instance;
    private List<Vehicle> vehicles = new ArrayList<>();
    private List<Customer> customers = new ArrayList<>();
    private RentalHistory rentalHistory = new RentalHistory();

    private RentalSystem() {
    	loadData();
    }
    
    
    private void loadData() {
        // Load vehicles from vehicles.txt
        try (BufferedReader vehicleReader = new BufferedReader(new FileReader("vehicles.txt"))) {
            String line;
            while ((line = vehicleReader.readLine()) != null) {
                String[] vehicleData = line.split("\\|");
                if (vehicleData.length >= 6) {
                    String licensePlate = vehicleData[1].trim();
                    String make = vehicleData[2].trim();
                    String model = vehicleData[3].trim();
                    int year = Integer.parseInt(vehicleData[4].trim());
                    Vehicle.VehicleStatus status = Vehicle.VehicleStatus.valueOf(vehicleData[5].trim());
                    String additionalInfo = vehicleData[6].trim();  
                    
                    Vehicle vehicle = null;

                    // Determine the type of vehicle and initialize accordingly
                    if (additionalInfo.startsWith("Seats:")) {
                        int numSeats = Integer.parseInt(additionalInfo.split(":")[1].trim());
                        vehicle = new Car(make, model, year, numSeats);
                    } else if (additionalInfo.startsWith("Sidecar:")) {
                        boolean hasSidecar = additionalInfo.split(":")[1].trim().equals("Yes");
                        vehicle = new Motorcycle(make, model, year, hasSidecar);
                    } else if (additionalInfo.startsWith("Cargo Capacity:")) {
                        double cargoCapacity = Double.parseDouble(additionalInfo.split(":")[1].trim());
                        vehicle = new Truck(make, model, year, cargoCapacity);
                    } else if (additionalInfo.startsWith("Horsepower:")) {
                        int horsepower = Integer.parseInt(additionalInfo.split(":")[1].trim());
                        boolean hasTurbo = additionalInfo.split(":")[2].trim().equals("Yes");
                        int numSeats = Integer.parseInt(vehicleData[7].trim()); 
                        vehicle = new SportCar(make, model, year, numSeats, horsepower, hasTurbo);
                    }

                    if (vehicle != null) {
                        vehicle.setLicensePlate(licensePlate);  
                        vehicle.setStatus(status);  
                        vehicles.add(vehicle);  
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("An error occurred while loading vehicles: " + e.getMessage());
        }

        // Load customers from customers.txt
        try (BufferedReader customerReader = new BufferedReader(new FileReader("customers.txt"))) {
            String line;
            while ((line = customerReader.readLine()) != null) {
                // Split by the '|' symbol to separate the ID and Name
                String[] customerData = line.split("\\|");
                
                // Log to see how the line is being split
                System.out.println("Customer Data Line: " + line);
                System.out.println("customerData length: " + customerData.length);
                for (String part : customerData) {
                    System.out.println("Part: " + part);
                }
                
                if (customerData.length == 2) {
                    // Extract the ID part
                    String idAsString = customerData[0].split(":")[1].trim();
                    System.out.println("ID as String: " + idAsString);
                    
                    // Safely parse the ID as an integer
                    try {
                        int customerId = Integer.parseInt(idAsString);
                        System.out.println("Parsed Customer ID: " + customerId);
                        
                        // Extract the Name part
                        String customerName = customerData[1].split(":")[1].trim();  
                        System.out.println("Customer Name: " + customerName);
                        
                        // Create the customer and add to the list
                        Customer customer = new Customer(customerId, customerName);
                        customers.add(customer);
                    } catch (NumberFormatException e) {
                        System.out.println("Error parsing customer ID: " + idAsString);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("An error occurred while loading customers: " + e.getMessage());
        }


        // Load rental records from rental_records.txt
        try (BufferedReader recordReader = new BufferedReader(new FileReader("rental_records.txt"))) {
            String line;
            while ((line = recordReader.readLine()) != null) {
                
                String[] recordData = line.split("\\|");
                if (recordData.length == 5) {
                    String vehiclePlate = recordData[1].split(":")[1].trim();
                    String customerName = recordData[2].split(":")[1].trim();
                    LocalDate date = LocalDate.parse(recordData[3].split(":")[1].trim());
                    String amountStr = recordData[4].split(":")[1].trim();
                    amountStr = amountStr.replaceAll("[^0-9.]", "");  
                    double amount = Double.parseDouble(amountStr);


                    Vehicle vehicle = findVehicleByPlate(vehiclePlate);
                    Customer customer = findCustomerById(customerName);
                    
                    if (vehicle != null && customer != null) {
                        RentalRecord record = new RentalRecord(vehicle, customer, date, amount, "RENT");
                        rentalHistory.addRecord(record);
                    }
                }
            }
        } catch (IOException e) {
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
    	try (BufferedWriter writer = new BufferedWriter(new FileWriter("vehicles.txt", 
    			true))) {
            writer.write(vehicle.getInfo());
            writer.newLine(); 
        } catch (IOException e) {
            System.out.println("An error occurred while saving the vehicle: " + 
        e.getMessage());
        }
    }
    
    public void saveCustomer(Customer customer) {
    	try (BufferedWriter writer = new BufferedWriter(new FileWriter("customers.txt", 
    			true))) {
            writer.write(customer.toString());
            writer.newLine();  
        } catch (IOException e) {
            System.out.println("An error occurred while saving the customer: " + 
        e.getMessage());
        }
    }
    
    public void saveRecord(RentalRecord record) {
    	try (BufferedWriter writer = new BufferedWriter(new FileWriter
    			("rental_records.txt", true))) {
            writer.write(record.toString());
            writer.newLine();  
        } catch (IOException e) {
            System.out.println("An error occurred while saving the rental record: " + 
        e.getMessage());
        }
    }
}