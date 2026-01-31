package logging;

import java.util.ArrayList;
import java.util.List;

public interface LogSubject {
    List<Observer> observers = new ArrayList<>();

    default void attach(Observer observer) {
        observers.add(observer);
    }

    default void detach(Observer observer) {
        observers.remove(observer);
    }

    default void notifyObservers(String message, LogLevel level) {
        for (Observer observer : observers) {
            observer.update(message, level);
        }
    }
} 