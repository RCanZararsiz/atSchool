package reservation_commands;

// Rezervasyon yapma ve iptal etme işlemleri için Command arayüzü
public interface Command {
    boolean execute();
    void undo();
} 