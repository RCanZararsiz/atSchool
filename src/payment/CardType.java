package payment;

public enum CardType {
    VISA("Visa"),
    MASTERCARD("Mastercard"),
    AMERICAN_EXPRESS("American Express");

    private final String displayName;

    CardType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static CardType fromCardNumber(String cardNumber) {
        // Kart numarasını temizle (boşluk ve tire işaretlerini kaldır)
        String cleanNumber = cardNumber.replaceAll("[\\s-]", "");
        
        // Sadece rakam kontrolü
        if (!cleanNumber.matches("^[0-9]+$")) {
            throw new IllegalArgumentException("Kart numarası sadece rakamlardan oluşmalıdır.");
        }

        // Kart tipine göre basit kontrol
        if (cleanNumber.startsWith("4") && cleanNumber.length() == 16) {
            return VISA;
        } else if ((cleanNumber.startsWith("51") || cleanNumber.startsWith("52") || 
                   cleanNumber.startsWith("53") || cleanNumber.startsWith("54") || 
                   cleanNumber.startsWith("55")) && cleanNumber.length() == 16) {
            return MASTERCARD;
        } else if ((cleanNumber.startsWith("34") || cleanNumber.startsWith("37")) && 
                   cleanNumber.length() == 15) {
            return AMERICAN_EXPRESS;
        }

        throw new IllegalArgumentException("Geçersiz kart numarası veya desteklenmeyen kart tipi.");
    }

    public static String[] getCardTypes() {
        CardType[] types = values();
        String[] names = new String[types.length];
        for (int i = 0; i < types.length; i++) {
            names[i] = types[i].getDisplayName();
        }
        return names;
    }
} 