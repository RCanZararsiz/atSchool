package payment;

import javax.swing.*;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import java.awt.*;

public class PaymentForm extends JDialog {
    private JTextField cardNumberField;
    private JTextField cardHolderField;
    private JTextField expiryDateField;
    private JTextField cvvField;
    private JComboBox<String> cardTypeCombo;
    private JLabel totalAmountLabel;
    private JLabel processingFeeLabel;
    private JLabel finalAmountLabel;
    private JLabel cardFormatLabel;
    private double amount;
    private CreditCardPayment payment;
    private boolean paymentSuccessful = false;

    public PaymentForm(Frame parent, double amount) {
        super(parent, "Ödeme Formu", true);
        this.amount = amount;
        setupUI();
    }

    private void setupUI() {
        setLayout(new BorderLayout());
        setSize(400, 500);
        setLocationRelativeTo(null);

        // Ana panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Kart türü seçimi
        JPanel cardTypePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cardTypePanel.add(new JLabel("Kart Türü:"));
        cardTypeCombo = new JComboBox<>(new String[]{"Visa", "Mastercard", "American Express"});
        cardTypePanel.add(cardTypeCombo);
        mainPanel.add(cardTypePanel);
        mainPanel.add(Box.createVerticalStrut(5));

        // Format bilgisi etiketi
        cardFormatLabel = new JLabel("Format: XXXX XXXX XXXX XXXX");
        cardFormatLabel.setForeground(new Color(100, 100, 100));
        cardFormatLabel.setFont(new Font(cardFormatLabel.getFont().getName(), Font.ITALIC, 11));
        JPanel formatPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        formatPanel.add(cardFormatLabel);
        mainPanel.add(formatPanel);
        mainPanel.add(Box.createVerticalStrut(5));

        // Kart numarası
        JPanel cardNumberPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cardNumberPanel.add(new JLabel("Kart Numarası:"));
        cardNumberField = new JTextField(20);
        cardNumberField.setDocument(new JTextFieldLimit(19, true));
        cardNumberPanel.add(cardNumberField);
        mainPanel.add(cardNumberPanel);
        mainPanel.add(Box.createVerticalStrut(10));

        // Kart sahibi
        JPanel cardHolderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cardHolderPanel.add(new JLabel("Kart Sahibi:"));
        cardHolderField = new JTextField(20);
        cardHolderPanel.add(cardHolderField);
        mainPanel.add(cardHolderPanel);
        mainPanel.add(Box.createVerticalStrut(10));

        // Son kullanma tarihi
        JPanel expiryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        expiryPanel.add(new JLabel("Son Kullanma Tarihi (AA/YY):"));
        expiryDateField = new JTextField(5);
        expiryDateField.setDocument(new JTextFieldLimit(5));
        expiryPanel.add(expiryDateField);
        mainPanel.add(expiryPanel);
        mainPanel.add(Box.createVerticalStrut(10));

        // CVV
        JPanel cvvPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cvvPanel.add(new JLabel("CVV:"));
        cvvField = new JTextField(4);
        cvvField.setDocument(new JTextFieldLimit(3)); // Başlangıçta 3 hane
        cvvPanel.add(cvvField);
        mainPanel.add(cvvPanel);
        mainPanel.add(Box.createVerticalStrut(20));

        // Tutar bilgileri
        JPanel amountPanel = new JPanel();
        amountPanel.setLayout(new BoxLayout(amountPanel, BoxLayout.Y_AXIS));
        amountPanel.setBorder(BorderFactory.createTitledBorder("Ödeme Detayları"));

        totalAmountLabel = new JLabel(String.format("Tutar: %.2f TL", amount));
        amountPanel.add(totalAmountLabel);

        processingFeeLabel = new JLabel("İşlem Ücreti: Hesaplanıyor...");
        amountPanel.add(processingFeeLabel);

        finalAmountLabel = new JLabel("Toplam Tutar: Hesaplanıyor...");
        amountPanel.add(finalAmountLabel);

        mainPanel.add(amountPanel);
        mainPanel.add(Box.createVerticalStrut(20));

        // Butonlar
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton payButton = new JButton("Ödemeyi Tamamla");
        JButton cancelButton = new JButton("İptal");

        payButton.addActionListener(e -> processPayment());
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(payButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel);

        // Event Listeners
        cardTypeCombo.addActionListener(e -> {
            // Kart numarası alanını temizle
            cardNumberField.setText("");
            cvvField.setText("");
            
            // Yeni kart türüne göre ayarları güncelle
            updateCardFormatLabel();
            updateCardNumberFieldLimit();
            updateCvvFieldLimit();
            updateCardValidation(); // İşlem ücretlerini güncelle
        });
        
        // İlk format etiketini ve CVV limitini ayarla
        updateCardFormatLabel();
        updateCvvFieldLimit();

        // Kart numarası değişikliklerini dinle
        DocumentListener cardNumberListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                SwingUtilities.invokeLater(() -> updateCardValidation());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                SwingUtilities.invokeLater(() -> updateCardValidation());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                SwingUtilities.invokeLater(() -> updateCardValidation());
            }
        };
        
        cardNumberField.getDocument().addDocumentListener(cardNumberListener);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void updateCardFormatLabel() {
        String selectedType = (String) cardTypeCombo.getSelectedItem();
        if (selectedType == null) return;
        
        String format;
        switch (selectedType) {
            case "Visa":
                format = "Format: XXXX XXXX XXXX XXXX (16 haneli, 4 ile başlar)";
                break;
            case "Mastercard":
                format = "Format: XXXX XXXX XXXX XXXX (16 haneli, 51-55 ile başlar)";
                break;
            case "American Express":
                format = "Format: XXXX XXXXXX XXXXX (15 haneli, 34 veya 37 ile başlar)";
                break;
            default:
                format = "Format: XXXX XXXX XXXX XXXX";
        }
        cardFormatLabel.setText(format);
    }

    private void updateCardNumberFieldLimit() {
        String selectedType = (String) cardTypeCombo.getSelectedItem();
        if (selectedType == null) return;
        
        int maxLength = selectedType.equals("American Express") ? 17 : 19; // Boşluklar dahil
        
        // Mevcut metni koru
        String currentText = cardNumberField.getText();
        cardNumberField.setDocument(new JTextFieldLimit(maxLength, true));
        cardNumberField.setText(currentText);
    }

    private void updateCvvFieldLimit() {
        String selectedType = (String) cardTypeCombo.getSelectedItem();
        if (selectedType == null) return;
        
        int cvvLength = selectedType.equals("American Express") ? 4 : 3;
        
        // Mevcut CVV metni koru (eğer yeni limite uyuyorsa)
        String currentCvv = cvvField.getText();
        cvvField.setDocument(new JTextFieldLimit(cvvLength));
        if (currentCvv.length() <= cvvLength) {
            cvvField.setText(currentCvv);
        } else {
            cvvField.setText("");
        }
    }

    private void updateCardValidation() {
        String cardNumber = cardNumberField.getText().replaceAll("\\s", "");
        if (cardNumber.length() < 13) {
            processingFeeLabel.setText("İşlem Ücreti: --");
            finalAmountLabel.setText("Toplam Tutar: --");
            return;
        }

        try {
            // CardType enum'unun var olduğunu varsayıyoruz
            CardType cardType = CardType.fromCardNumber(cardNumber);
            
            // Kart türünü otomatik seç (sonsuz döngüyü önlemek için kontrol et)
            String detectedCardType = cardType.getDisplayName();
            String currentSelection = (String) cardTypeCombo.getSelectedItem();
            if (!detectedCardType.equals(currentSelection)) {
                cardTypeCombo.removeActionListener(cardTypeCombo.getActionListeners()[0]); // Geçici olarak listener'ı kaldır
                cardTypeCombo.setSelectedItem(detectedCardType);
                cardTypeCombo.addActionListener(e -> {
                    cardNumberField.setText("");
                    cvvField.setText("");
                    updateCardFormatLabel();
                    updateCardNumberFieldLimit();
                    updateCvvFieldLimit();
                    updateCardValidation();
                }); // Listener'ı geri ekle
            }
            
            updateCardFormatLabel();
            
            // İşlem ücretini hesapla
            double fee = calculateProcessingFee(amount, cardType);
            processingFeeLabel.setText(String.format("İşlem Ücreti: %.2f TL", fee));
            finalAmountLabel.setText(String.format("Toplam Tutar: %.2f TL", amount + fee));
            
        } catch (IllegalArgumentException e) {
            processingFeeLabel.setText("İşlem Ücreti: --");
            finalAmountLabel.setText("Toplam Tutar: --");
        } catch (Exception e) {
            // Beklenmeyen hatalar için
            processingFeeLabel.setText("İşlem Ücreti: --");
            finalAmountLabel.setText("Toplam Tutar: --");
        }
    }

    private double calculateProcessingFee(double amount, CardType cardType) {
        switch (cardType) {
            case VISA: return amount * 0.015;
            case MASTERCARD: return amount * 0.02;
            case AMERICAN_EXPRESS: return amount * 0.025;
            default: return 0;
        }
    }

    private void processPayment() {
        try {
            String cardNumber = cardNumberField.getText().replaceAll("\\s", "");
            String cardHolder = cardHolderField.getText().trim();
            String expiryDate = expiryDateField.getText().trim();
            String cvv = cvvField.getText().trim();

            // Temel validasyon
            if (cardNumber.isEmpty() || cardHolder.isEmpty() || expiryDate.isEmpty() || cvv.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Lütfen tüm alanları doldurun.", 
                    "Hata", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Kart numarası kontrolü
            if (!cardNumber.matches("^[0-9]+$")) {
                JOptionPane.showMessageDialog(this,
                    "Kart numarası sadece rakamlardan oluşmalıdır.",
                    "Hata",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Kart numarası uzunluk kontrolü
            CardType cardType;
            try {
                cardType = CardType.fromCardNumber(cardNumber);
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(this,
                    "Geçersiz kart numarası.",
                    "Hata",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Son kullanma tarihi formatı kontrolü
            if (!expiryDate.matches("^(0[1-9]|1[0-2])/([0-9]{2})$")) {
                JOptionPane.showMessageDialog(this,
                    "Geçersiz son kullanma tarihi formatı. (AA/YY)",
                    "Hata",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // CVV kontrolü
            String selectedType = (String) cardTypeCombo.getSelectedItem();
            int expectedCvvLength = selectedType != null && selectedType.equals("American Express") ? 4 : 3;
            if (cvv.length() != expectedCvvLength || !cvv.matches("^[0-9]+$")) {
                JOptionPane.showMessageDialog(this,
                    selectedType + " kartları için CVV " + expectedCvvLength + " haneli rakam olmalıdır.",
                    "Hata",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Ödeme işlemini gerçekleştir
            payment = new CreditCardPayment(cardNumber, cardHolder, expiryDate, cvv);
            paymentSuccessful = true;
            dispose();

        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, 
                e.getMessage(), 
                "Ödeme Hatası", 
                JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Beklenmeyen bir hata oluştu: " + e.getMessage(), 
                "Hata", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    public CreditCardPayment getPayment() {
        return payment;
    }

    public boolean isPaymentSuccessful() {
        return paymentSuccessful;
    }

    private class JTextFieldLimit extends javax.swing.text.PlainDocument {
        private final int limit;
        private final boolean formatCardNumber;

        JTextFieldLimit(int limit) {
            this(limit, false);
        }

        JTextFieldLimit(int limit, boolean formatCardNumber) {
            super();
            this.limit = limit;
            this.formatCardNumber = formatCardNumber;
        }

        @Override
        public void insertString(int offset, String str, javax.swing.text.AttributeSet attr) 
                throws javax.swing.text.BadLocationException {
            if (str == null) return;

            // Kart numarası formatlaması için sadece rakam girişine izin ver
            if (formatCardNumber && !str.matches("[0-9\\s]")) {
                return;
            }

            String currentText = getText(0, getLength());
            String beforeOffset = currentText.substring(0, offset);
            String afterOffset = currentText.substring(offset);
            String newText = beforeOffset + str + afterOffset;

            // Kart numarası formatlaması
            if (formatCardNumber) {
                // Boşlukları kaldır ve sadece rakamları al
                String digitsOnly = newText.replaceAll("\\s", "");
                
                // Maximum digit kontrolü
                int maxDigits = (limit == 17) ? 15 : 16; // American Express 15 hane, diğerleri 16
                if (digitsOnly.length() > maxDigits) {
                    return;
                }
                
                // Formatla
                StringBuilder formatted = new StringBuilder();
                
                if (limit == 17) { // American Express formatı (4-6-5)
                    for (int i = 0; i < digitsOnly.length(); i++) {
                        if (i == 4 || i == 10) {
                            formatted.append(" ");
                        }
                        formatted.append(digitsOnly.charAt(i));
                    }
                } else { // Visa ve Mastercard formatı (4-4-4-4)
                    for (int i = 0; i < digitsOnly.length(); i++) {
                        if (i > 0 && i % 4 == 0) {
                            formatted.append(" ");
                        }
                        formatted.append(digitsOnly.charAt(i));
                    }
                }
                
                newText = formatted.toString();
            }

            // Final uzunluk kontrolü
            if (newText.length() <= limit) {
                super.remove(0, getLength());
                super.insertString(0, newText, attr);
            }
        }
    }
}