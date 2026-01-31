package payment;

public class PaymentContext {
    private Strategy paymentStrategy;

    public void setPaymentStrategy(Strategy strategy) {
        this.paymentStrategy = strategy;
    }

    public boolean executePayment(double amount) {
        if (paymentStrategy == null) {
            throw new IllegalStateException("Ödeme yöntemi seçilmedi.");
        }
        return paymentStrategy.processPayment(amount);
    }

    public String getCurrentPaymentMethod() {
        if (paymentStrategy == null) {
            throw new IllegalStateException("Ödeme yöntemi seçilmedi.");
        }
        return paymentStrategy.getPaymentMethod();
    }
} 