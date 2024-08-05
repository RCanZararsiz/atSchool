/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package movieapp;

/**
 *
 * @author canca
 */
public class Node {
    
    private Movie movie;
    private Node prev;
    private Node next;

    public Node(Movie movie) {
        this.movie = movie;
        this.prev = null;
        this.next = null;
    }

    /**
     * @return the movie
     */
    public Movie getMovie() {
        return movie;
    }

    /**
     * @param movie the movie to set
     */
    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    /**
     * @return the prev
     */
    public Node getPrev() {
        return prev;
    }

    /**
     * @param prev the prev to set
     */
    public void setPrev(Node prev) {
        this.prev = prev;
    }

    /**
     * @return the next
     */
    public Node getNext() {
        return next;
    }

    /**
     * @param next the next to set
     */
    public void setNext(Node next) {
        this.next = next;
    }
}
