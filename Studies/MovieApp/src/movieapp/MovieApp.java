/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package movieapp;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.JOptionPane;

/**
 *
 * @author canca
 */
public class MovieApp {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        Scanner scanner = new Scanner(System.in);
        
        DoublyLinkedList filmInventory = new DoublyLinkedList();
        readFromFile(filmInventory, "bilgiler.txt");
        
        
        int choice;
        do {
            displayMenu();
            String ab = JOptionPane.showInputDialog(null, "1) Metin dosyasından filmleri oku ve liste oluştur.\n"
                + "2) Yeni film ekle.\n"
                + "3) Film bilgilerini göster\n"
                + "4) Film sil.\n"
                + "5) Tüm filmleri tarihlerine göre sırala(eskiden yeniye).\n"
                + "6) Tüm filmleri tarihlerine göre sırala(yeniden eskiye).\n"
                + "7) İstediğiniz bir tarihten önce çıkan filmleri yazdır.\n"
                + "8) Çıkış\n" + "Lütfen seçiminizi yapınız","***FİLM ENVANTERİ UYGULAMASI***" , JOptionPane.PLAIN_MESSAGE);
            choice = Integer.parseInt(ab);

            switch (choice) {
                case 1:
                    DoublyLinkedList newList = new DoublyLinkedList();
                    readFromFile(newList, "bilgiler.txt");
                    filmInventory = newList;
                    JOptionPane.showMessageDialog(null, "Dosyadaki filmlerin listesi oluşturuldu.", "***FİLM ENVANTERİ UYGULAMASI***", JOptionPane.PLAIN_MESSAGE);
                    break;
                case 2:
                    addFilm(scanner, filmInventory);
                    break;
                case 3:
                    displayFilm(scanner, filmInventory);
                    break;
                case 4:
                    removeFilm(scanner, filmInventory);
                    break;
                case 5:
                    filmInventory.printForward();
                    break;
                case 6:
                    filmInventory.printBackward();
                    break;
                case 7:
                    displayFilmsBeforeYear(scanner, filmInventory);
                    break;
                case 8:
                    JOptionPane.showMessageDialog(null, "Veriler dosyaya kaydedildi. Uygulama kapatiliyor...", "***FİLM ENVANTERİ UYGULAMASI***", JOptionPane.PLAIN_MESSAGE);
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "Gecersiz secim. Tekrar deneyin.", "***FİLM ENVANTERİ UYGULAMASI***", JOptionPane.PLAIN_MESSAGE);
            }
        } while (choice != 8);

        scanner.close();
    }
    
    private static void displayMenu() {
        JOptionPane.showMessageDialog(null, "UYGULAMAMIZA HOŞGELDİNİZ", "***FİLM ENVANTERİ UYGULAMASI***", JOptionPane.PLAIN_MESSAGE);
    }
        
    private static void readFromFile(DoublyLinkedList filmInventory, String filename) {
          try (Scanner fileScanner = new Scanner(new File(filename))) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                String[] parts = line.split(",");

                int year = Integer.parseInt(parts[0].trim());
                String title = parts[1].trim();
                String genre = parts[2].trim();
                String director = parts[3].trim();

                ArrayList<Actor> actors = new ArrayList<>();
                    for (int i = 4; i < parts.length; i += 3) {
                        String actorName = parts[i].trim();
                        String actorGender = parts[i + 1].trim();
                        String actorNationality = parts[i + 2].trim();
                    
                        Actor actor = new Actor(actorName, actorGender, actorNationality);
                        actors.add(actor);
                    }

                Movie movie = new Movie(year, title, genre, director, actors);
                filmInventory.addMovie(movie);
            }
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Dosya bulunamadı" + e.getMessage(), "***FİLM ENVANTERİ UYGULAMASI***", JOptionPane.PLAIN_MESSAGE);
        }
    }
     
    private static void addFilm(Scanner scanner, DoublyLinkedList filmInventory) {
        String yil = JOptionPane.showInputDialog(null, "Film yapım yılını giriniz","***FİLM ENVANTERİ UYGULAMASI***", JOptionPane.PLAIN_MESSAGE);
        int year = Integer.parseInt(yil);

        String title = JOptionPane.showInputDialog(null, "Film adını giriniz","***FİLM ENVANTERİ UYGULAMASI***", JOptionPane.PLAIN_MESSAGE);
        
        String genre = JOptionPane.showInputDialog(null, "Film türünü giriniz","***FİLM ENVANTERİ UYGULAMASI***", JOptionPane.PLAIN_MESSAGE);
        
        String director = JOptionPane.showInputDialog(null, "Filmin yönetmenini giriniz","***FİLM ENVANTERİ UYGULAMASI***", JOptionPane.PLAIN_MESSAGE);

        ArrayList<Actor> actors = new ArrayList<>();
        char choice;
        do {
            String actorName = JOptionPane.showInputDialog(null, "Filmdeki oyuncunun adını giriniz","***FİLM ENVANTERİ UYGULAMASI***", JOptionPane.PLAIN_MESSAGE);

            String actorGender = JOptionPane.showInputDialog(null, "Oyuncunun cinsiyetini giriniz","***FİLM ENVANTERİ UYGULAMASI***", JOptionPane.PLAIN_MESSAGE);

            String actorNationality = JOptionPane.showInputDialog(null, "oyuncunun hangi ülkenin vatandaşı olduğunu giriniz","***FİLM ENVANTERİ UYGULAMASI***", JOptionPane.PLAIN_MESSAGE);

            Actor actor = new Actor(actorName, actorGender, actorNationality);
            actors.add(actor);

            String cs = JOptionPane.showInputDialog(null, "Başka bir oyuncu eklemek istiyor musunuz? (E/H)","***FİLM ENVANTERİ UYGULAMASI***", JOptionPane.PLAIN_MESSAGE);
            choice = cs.charAt(0);
        } while (choice == 'E' || choice == 'e');

        Movie movie = new Movie(year, title, genre, director, actors);
        filmInventory.addMovie(movie);

        JOptionPane.showMessageDialog(null, "Film başarıyla eklendi.", "***FİLM ENVANTERİ UYGULAMASI***", JOptionPane.PLAIN_MESSAGE);
        
        filmInventory.saveToFile("bilgiler.txt");
    }
    
    private static void displayFilm(Scanner scanner, DoublyLinkedList filmInventory) {
        String title = JOptionPane.showInputDialog(null, "Film adını giriniz","***FİLM ENVANTERİ UYGULAMASI***", JOptionPane.PLAIN_MESSAGE);
        

        Node current = filmInventory.getHead();
        while (current != null) {
            if (current.getMovie().getTitle().equalsIgnoreCase(title)) {
        JOptionPane.showMessageDialog(null,current.getMovie(),"***FİLM ENVANTERİ UYGULAMASI***", JOptionPane.PLAIN_MESSAGE);
                return;
            }
            current = current.getNext();
        }

        JOptionPane.showMessageDialog(null, "Belirtilen ada sahip film bulunamadı.","***FİLM ENVANTERİ UYGULAMASI***", JOptionPane.PLAIN_MESSAGE);
    }

    private static void removeFilm(Scanner scanner, DoublyLinkedList filmInventory) {
    String title = JOptionPane.showInputDialog(null, "Film adını giriniz","***FİLM ENVANTERİ UYGULAMASI***", JOptionPane.PLAIN_MESSAGE);

    filmInventory.removeMovie(title);
    JOptionPane.showMessageDialog(null, "Film başarıyla silindi.","***FİLM ENVANTERİ UYGULAMASI***", JOptionPane.PLAIN_MESSAGE);

    filmInventory.saveToFile("bilgiler.txt");
}

    private static void displayFilmsBeforeYear(Scanner scanner, DoublyLinkedList filmInventory) {
        String yil = JOptionPane.showInputDialog(null, "Hangi yıldan önceki filmleri görmek istiyorsunuz?","***FİLM ENVANTERİ UYGULAMASI***", JOptionPane.PLAIN_MESSAGE);
        int year = Integer.parseInt(yil);
        

        filmInventory.printMoviesBeforeYear(year);
    }
    
}
