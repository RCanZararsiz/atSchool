/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vize;

import java.util.Objects;

/**
 *
 * @author canca
 */
public abstract class Vehicle {
    
    private String brand;
    private String licencePlate;
    private Person owner;

    public Vehicle() {
        this.brand = null;
        this.licencePlate = null;
        this.owner = null;
    }

    public Vehicle(String brand, String licencePlate, Person owner) {
        this.brand = brand;
        this.licencePlate = licencePlate;
        this.owner = owner != null ? new Person(owner) : null;
    }

    public Vehicle(Vehicle other) {
        this.brand = other.brand;
        this.licencePlate = other.licencePlate;
        this.owner = other.owner != null ? new Person(other.owner) : null;
    }

    public String getBrand() {
        return this.brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getLicencePlate() {
        return this.licencePlate;
    }

    public void setLicencePlate(String licencePlate) {
        this.licencePlate = licencePlate;
    }

    public Person getOwner() {
        return this.owner != null ? new Person(this.owner) : null;
    }

    public void setOwner(Person owner) {
        this.owner = owner != null ? new Person(owner) : null;
    }

    public abstract int calculateTax();

    @Override
    public String toString() {
        return "Brand: " + this.brand + "\n" + "Licence Plate: " + this.licencePlate + "\n" +
                "Owner: " + (this.owner != null ? this.owner.toString() : "N/A");
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof Vehicle)) {
            return false;
        }

        Vehicle other = (Vehicle) obj;

        return Objects.equals(this.brand, other.brand) &&
                Objects.equals(this.licencePlate, other.licencePlate) &&
                Objects.equals(this.owner, other.owner);
    }
    
}
