/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package vize;

/**
 *
 * @author canca
 */
public class Demo {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        ContactInfo contactInfo = new ContactInfo("Canakkale", "0555-555 55 55", "bestdriver@gmail.com");
        
        
        Person person = new Person("Can Zararsiz", contactInfo);
        
        
        Bus bus = new Bus("Temsa-Prenses", "17 AB 1881", person, 46, 9);
        
        
        System.out.println("Bus Information:\n" + bus.toString());
        
        
        int tax = bus.calculateTax();
        System.out.println("Tax to be paid: " + tax + " TL");
        
        
        Bus busCopy = new Bus(bus);
        
        
        if (bus.equals(busCopy)) {
            System.out.println("Bus objects are equal.");
        } else {
            System.out.println("Bus objects are not equal.");
        }
        

        
    }
    
}
