import java.io.Serializable;

public abstract class Vehicle implements Serializable {
	private static final long serialVersionUID = 111L;
	
    private String licensePlate;
    private String make;
    private String model;
    private int year;
    private VehicleStatus status;

    public enum VehicleStatus { AVAILABLE, RESERVED, RENTED, MAINTENANCE, OUTOFSERVICE }

    public Vehicle(String make, String model, int year) {
    	this.make = capitalize(make);
    	this.model = capitalize(model);
    	
        this.year = year;
        this.status = VehicleStatus.AVAILABLE;
        this.licensePlate = null;
    }

	private String capitalize(String input) {
		if (input == null || input.isEmpty())
    		input = null;
    	else
    		input = input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
		return input;
	}

    public Vehicle() {
        this(null, null, 0);
    }

    public void setLicensePlate(String plate) throws IllegalArgumentException{
    	if (isValidPlate(plate)) 
            this.licensePlate = plate.toUpperCase();
    	else
    		throw new IllegalArgumentException("License plate is invalid.");
    }

    private Boolean isValidPlate(String plate) {
		int i = 0;
		int flag = 0;
		
		if (plate == null) 
			return false;
		if (plate.isEmpty())
			return false;
		if (plate.length() != 6)
			return false;
		
		else {
			for (char c : plate.toCharArray()) {
				if (i < 3) {
					if ((c >= '0') && (c <= '9')) {
						flag = 1;
					}
				}
				else if (i >= 3) {
					if (!(c >= '0') && !(c <= '9')) {
						flag = 1;
					}
				}
				i++;
			}
		}
		if (flag == 1)
			return false;
		else
			return true;
	}
    
    public void setStatus(VehicleStatus status) {
    	this.status = status;
    }

    public String getLicensePlate() { return licensePlate; }

    public String getMake() { return make; }

    public String getModel() { return model;}

    public int getYear() { return year; }

    public VehicleStatus getStatus() { return status; }

    public String getInfo() {
        return "| " + licensePlate + " | " + make + " | " + model + " | " + year + " | " + status + " |";
    }

}
