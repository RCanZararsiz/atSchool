/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package movieapp;

import java.util.ArrayList;

/**
 *
 * @author canca
 */
public class Movie {
    
    private int year;
    private String title;
    private String genre;
    private String director;
    private ArrayList<Actor> actors;

    public Movie(int year, String title, String genre, String director, ArrayList<Actor> actors) {
        this.year = year;
        this.title = title;
        this.genre = genre;
        this.director = director;
        this.actors = actors;
    }

    /**
     * @return the year
     */
    public int getYear() {
        return year;
    }

    /**
     * @param year the year to set
     */
    public void setYear(int year) {
        this.year = year;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the genre
     */
    public String getGenre() {
        return genre;
    }

    /**
     * @param genre the genre to set
     */
    public void setGenre(String genre) {
        this.genre = genre;
    }

    /**
     * @return the director
     */
    public String getDirector() {
        return director;
    }

    /**
     * @param director the director to set
     */
    public void setDirector(String director) {
        this.director = director;
    }

    /**
     * @return the actors
     */
    public ArrayList<Actor> getActors() {
        return actors;
    }

    /**
     * @param actors the actors to set
     */
    public void setActors(ArrayList<Actor> actors) {
        this.actors = actors;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Yil: ").append(year).append("\n");
        sb.append("Film Adi: ").append(title).append("\n");
        sb.append("Tur: ").append(genre).append("\n");
        sb.append("Yonetmen: ").append(director).append("\n");
        sb.append("Oyuncular: ").append(actors).append("\n");
    
        return sb.toString();
    }
    
}
