package payment;

// Ödeme yöntemleri için Strategy arayüzü
public interface Strategy {
    boolean processPayment(double amount);
    String getPaymentMethod();
} 