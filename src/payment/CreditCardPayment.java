package payment;

public class CreditCardPayment implements Strategy {
    private String cardNumber;
    private String cardHolderName;
    private String expiryDate;
    private String cvv;
    private CardType cardType;

    public CreditCardPayment(String cardNumber, String cardHolderName, String expiryDate, String cvv) {
        validateCardDetails(cardNumber, expiryDate, cvv);
        this.cardNumber = cardNumber;
        this.cardHolderName = cardHolderName;
        this.expiryDate = expiryDate;
        this.cvv = cvv;
        this.cardType = CardType.fromCardNumber(cardNumber);
    }

    private void validateCardDetails(String cardNumber, String expiryDate, String cvv) {
        // Kart numarası validasyonu
        try {
            CardType.fromCardNumber(cardNumber);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }

        // Son kullanma tarihi validasyonu (MM/YY formatı)
        if (!expiryDate.matches("^(0[1-9]|1[0-2])/([0-9]{2})$")) {
            throw new IllegalArgumentException("Geçersiz son kullanma tarihi formatı. MM/YY formatı kullanınız.");
        }

        // CVV validasyonu
        CardType cardType = CardType.fromCardNumber(cardNumber);
        int expectedCvvLength = (cardType == CardType.AMERICAN_EXPRESS) ? 4 : 3;
        if (cvv.length() != expectedCvvLength) {
            throw new IllegalArgumentException("Geçersiz CVV kodu. " + cardType.getDisplayName() + " kartı için " + expectedCvvLength + " haneli bir CVV kodu gereklidir.");
        }

        // Son kullanma tarihi geçerliliği kontrolü
        if (isExpired(expiryDate)) {
            throw new IllegalArgumentException("Kartın son kullanma tarihi doldu.");
        }
    }

    private boolean isExpired(String expiryDate) {
        String[] parts = expiryDate.split("/");
        int month = Integer.parseInt(parts[0]);
        int year = Integer.parseInt("20" + parts[1]);
        
        java.time.YearMonth expiry = java.time.YearMonth.of(year, month);
        return expiry.isBefore(java.time.YearMonth.now());
    }

    @Override
    public boolean processPayment(double amount) {
        System.out.println("\n" + cardType.getDisplayName() + " ödemesi için " + amount + " TL ödeme yapılıyor.");
        System.out.println("Kart Numarası: " + maskCardNumber(cardNumber));
        System.out.println("Kart Sahibi: " + cardHolderName);
        System.out.println("Son Kullanma Tarihi: " + expiryDate);
        
        // Kart türüne özel işlem ücreti hesaplama
        double processingFee = calculateProcessingFee(amount);
        System.out.println("İşlem Ücreti: " + processingFee + " TL");
        System.out.println("Toplam Tutar: " + (amount + processingFee) + " TL");
        
        // Burada gerçek ödeme işlemi simüle edilecek
        System.out.println("Ödeme başarıyla tamamlandı!");
        return true;
    }

    private double calculateProcessingFee(double amount) {
        // Kart türüne göre işlem ücreti
        switch (cardType) {
            case VISA:
                return amount * 0.015; // %1.5
            case MASTERCARD:
                return amount * 0.02;  // %2
            case AMERICAN_EXPRESS:
                return amount * 0.025; // %2.5
            default:
                return 0;
        }
    }

    @Override
    public String getPaymentMethod() {
        return cardType.getDisplayName();
    }

    private String maskCardNumber(String cardNumber) {
        int visibleDigits = (cardType == CardType.AMERICAN_EXPRESS) ? 4 : 4;
        return "****-****-****-" + cardNumber.substring(cardNumber.length() - visibleDigits);
    }
} 