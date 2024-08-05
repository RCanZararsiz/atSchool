/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vize;

/**
 *
 * @author canca
 */
public class Bus extends Vehicle {
    
    private int capacity;
    private int ageOfBus;

    public Bus() {
        super();
        this.capacity = 0;
        this.ageOfBus = 0;
    }

    public Bus(String brand, String licencePlate, Person owner, int capacity, int ageOfBus) {
        super(brand, licencePlate, owner);
        this.capacity = capacity;
        this.ageOfBus = ageOfBus;
    }

    public Bus(Bus otherBus) {
        super(otherBus.getBrand(), otherBus.getLicencePlate(), otherBus.getOwner());
        this.capacity = otherBus.getCapacity();
        this.ageOfBus = otherBus.getAgeOfBus();
    }

    /**
     * @return the capacity
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * @param capacity the capacity to set
     */
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    /**
     * @return the ageOfBus
     */
    public int getAgeOfBus() {
        return ageOfBus;
    }

    /**
     * @param ageOfBus the ageOfBus to set
     */
    public void setAgeOfBus(int ageOfBus) {
        this.ageOfBus = ageOfBus;
    }

    @Override
    public int calculateTax() {
        if (getAgeOfBus() < 5) {
            return 4000;
        } else if (getAgeOfBus() >= 5 && getAgeOfBus() <= 10) {
            return 3000;
        } else {
            return 2000;
        }
    }

    @Override
    public String toString() {
        return "Bus: " + super.toString() + "\n" + "Capacity: " + getCapacity() + "\n" + "Age of Bus: " + getAgeOfBus();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Bus otherBus = (Bus) obj;
        return super.equals(otherBus) && getCapacity() == otherBus.getCapacity() && getAgeOfBus() == otherBus.getAgeOfBus();
    }

    
    
}
