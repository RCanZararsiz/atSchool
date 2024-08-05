/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package movieapp;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 *
 * @author canca
 */
public class DoublyLinkedList {
    
    private Node head;
    private Node tail;

    public DoublyLinkedList() {
        this.head = null;
        this.tail = null;
    }

    /**
     * @return the head
     */
    public Node getHead() {
        return head;
    }

    /**
     * @param head the head to set
     */
    public void setHead(Node head) {
        this.head = head;
    }

    /**
     * @return the tail
     */
    public Node getTail() {
        return tail;
    }

    /**
     * @param tail the tail to set
     */
    public void setTail(Node tail) {
        this.tail = tail;
    }

    public boolean isEmpty() {
        return getHead() == null;
    }
    
    public void addToEmptyList(Movie movie) {
        Node newNode = new Node(movie);
        setHead(newNode);
        setTail(newNode);
    }
    
    public void addSorted(Movie movie) {
        Node newNode = new Node(movie);
        if (isEmpty()) {
            addToEmptyList(movie);
        } else {
            Node current = getHead();
            while (current != null) {
                if (movie.getYear() < current.getMovie().getYear()) {
                    newNode.setNext(current);
                    newNode.setPrev(current.getPrev());
                    current.setPrev(newNode);
                    if (current == getHead()) {
                        setHead(newNode);
                    } else {
                        newNode.getPrev().setNext(newNode);
                    }
                    break;
                } else if (movie.getYear() == current.getMovie().getYear()) {
                    if (movie.getTitle().compareToIgnoreCase(current.getMovie().getTitle()) < 0) {
                        newNode.setNext(current);
                        newNode.setPrev(current.getPrev());
                        current.setPrev(newNode);
                        if (current == getHead()) {
                            setHead(newNode);
                        } else {
                            newNode.getPrev().setNext(newNode);
                        }
                        break;
                    }
                }
                if (current.getNext() == null) {
                    newNode.setPrev(current);
                    current.setNext(newNode);
                    setTail(newNode);
                    break;
                }
                current = current.getNext();
            }
        }
    }

    public void addMovie(Movie movie) {
        addSorted(movie);
    }
    
    public void removeMovie(String movieTitle) {
        Node current = getHead();
        while (current != null) {
            if (current.getMovie().getTitle().equalsIgnoreCase(movieTitle)) {
                if (current == getHead() && current == getTail()) {
                    setHead(null);
                    setTail(null);
                } else if (current == getHead()) {
                    setHead(current.getNext());
                    getHead().setPrev(null);
                } else if (current == getTail()) {
                    setTail(current.getPrev());
                    getTail().setNext(null);
                } else {
                    current.getPrev().setNext(current.getNext());
                    current.getNext().setPrev(current.getPrev());
                }
                break;
            }
            current = current.getNext();
        }
    }
    
    public void printForward() {
        Node current = getHead();
        while(current != null) {
            JOptionPane.showMessageDialog(null, current.getMovie(), "Filmlerin Eskiden Yeniye Doğru Sıralaması", JOptionPane.PLAIN_MESSAGE);
            current = current.getNext();
        }
    }
    
    public void printBackward() {
        Node current = getTail();
        while (current != null) {
            JOptionPane.showMessageDialog(null, current.getMovie(), "Filmlerin yeniden eskiye doğru sıralaması", JOptionPane.PLAIN_MESSAGE);
            current = current.getPrev();
        }
    }
    
    public void printMoviesBeforeYear(int year) {
        Node current = getHead();
        while (current != null) {
            if (current.getMovie().getYear() < year) {
                JOptionPane.showMessageDialog(null, current.getMovie(), "Seçtiğiniz tarihten önceki filmler", JOptionPane.PLAIN_MESSAGE);
            }
            current = current.getNext();
        }
    }
    
    public void saveToFile(String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            Node current = getHead();
            while (current != null) {
                writer.write(current.getMovie().getYear() + ",");
                writer.write(current.getMovie().getTitle() + ",");
                writer.write(current.getMovie().getGenre() + ",");
                writer.write(current.getMovie().getDirector() + ",");
                
                ArrayList<Actor> actors = current.getMovie().getActors();
                for (int i = 0; i < actors.size(); i++) {
                    Actor actor = actors.get(i);
                    writer.write(actor.getName() + "," + actor.getGender() + "," + actor.getNationality());
                    if (i < actors.size() - 1) {
                        writer.write(",");
                    }
                }
                writer.write("\n");
                current = current.getNext();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
}
