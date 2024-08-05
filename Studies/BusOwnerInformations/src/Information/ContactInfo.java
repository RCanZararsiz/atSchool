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
public class ContactInfo {
    
    private String homeAddress;
    private String telNumber;
    private String eMail;

    public ContactInfo() {
        this.homeAddress = null;
        this.telNumber = null;
        this.eMail = null;
    }

    public ContactInfo(String homeAddress, String telNumber, String eMail) {
        this.homeAddress = homeAddress;
        this.telNumber = telNumber;
        this.eMail = eMail;
    }

    public ContactInfo(ContactInfo contactInfo) {
        this.homeAddress = contactInfo.getHomeAddress();
        this.telNumber = contactInfo.getTelNumber();
        this.eMail = contactInfo.geteMail();
    }

    /**
     * @return the homeAddress
     */
    public String getHomeAddress() {
        return homeAddress;
    }

    /**
     * @param homeAddress the homeAddress to set
     */
    public void setHomeAddress(String homeAddress) {
        this.homeAddress = homeAddress;
    }

    /**
     * @return the telNumber
     */
    public String getTelNumber() {
        return telNumber;
    }

    /**
     * @param telNumber the telNumber to set
     */
    public void setTelNumber(String telNumber) {
        this.telNumber = telNumber;
    }

    /**
     * @return the eMail
     */
    public String geteMail() {
        return eMail;
    }

    /**
     * @param eMail the eMail to set
     */
    public void seteMail(String eMail) {
        this.eMail = eMail;
    }

     @Override
    public String toString() {
        return "Home Address: " + this.getHomeAddress() + "\n" +
                "Telephone Number: " + this.getTelNumber() + "\n" +
                "E-Mail: " + this.geteMail();
    }

    /**
     *
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ContactInfo)) {
            return false;
        }
        ContactInfo other = (ContactInfo) obj;
        return Objects.equals(homeAddress, other.homeAddress) && Objects.equals(telNumber, other.telNumber)
                && Objects.equals(eMail, other.eMail);
    }

    
    
}
