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
public class Person {
    
    private String name;
    private ContactInfo contactInformation;

    public Person() {
        this.name = null;
        this.contactInformation = null;
    }

    public Person(String name, ContactInfo contactInformation) {
        this.name = name;
        if (contactInformation != null) {
            this.contactInformation = new ContactInfo(contactInformation);
        } else {
            this.contactInformation = null;
        }
    }

    public Person(Person other) {
        this.name = other.name;
        if (other.contactInformation != null) {
            this.contactInformation = new ContactInfo(other.getContactInformation());
        } else {
            this.contactInformation = null;
        }
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the contactInformation
     */
    public ContactInfo getContactInformation() {
        return contactInformation;
    }

    /**
     * @param contactInformation the contactInformation to set
     */
    public void setContactInformation(ContactInfo contactInformation) {
        this.contactInformation = contactInformation;
    }

    @Override
    public String toString() {
        return "Name: " + getName() + "\n" + getContactInformation();
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
        if (!(obj instanceof Person)) {
            return false;
        }
        Person other = (Person) obj;
        return Objects.equals(getName(), other.getName()) && Objects.equals(getContactInformation(), other.getContactInformation());
    }

    
}
