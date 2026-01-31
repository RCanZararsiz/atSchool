import reservation_commands.Command;
import logging.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import payment.PaymentContext;
import payment.Strategy;
import payment.CreditCardPayment;
import payment.PaymentForm;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import java.awt.GridLayout;
import javax.swing.BorderFactory;

public class MakeReservationCommand implements Command {
    private int vehicleId;
    private int seatNumber;
    private int userId;
    private String vehicleType;
    private String username;
    private String vehicleInfo;
    private boolean executed = false;
    private static final double BASE_PRICE = 150.0; // Temel fiyat
    private LogService logService;
    private PaymentContext paymentContext;

    public MakeReservationCommand(int vehicleId, int seatNumber, int userId, String vehicleType) {
        this.vehicleId = vehicleId;
        this.seatNumber = seatNumber;
        this.userId = userId;
        this.vehicleType = vehicleType;
        this.username = ConnectDB.getUsername(userId);
        this.vehicleInfo = getVehicleInfo();
        this.logService = LogService.getInstance();
        this.paymentContext = new PaymentContext();
    }
    
    private String getVehicleInfo() {
        try {
            if (vehicleType.equalsIgnoreCase("otobüs")) {
                ResultSet rs = ConnectDB.getTripInfo(vehicleId);
                if (rs.next()) {
                    String busName = rs.getString("bus_name");
                    String date = rs.getString("trip_date");
                    String time = rs.getString("trip_time");
                    return busName + " - " + date + " " + time;
                }
            } else if (vehicleType.equalsIgnoreCase("uçak")) {
                ResultSet rs = ConnectDB.getFlightInfo(vehicleId);
                if (rs.next()) {
                    String planeName = rs.getString("plane_name");
                    String date = rs.getString("flight_date");
                    String time = rs.getString("flight_time");
                    return planeName + " - " + date + " " + time;
                }
            }
        } catch (SQLException e) {
            System.out.println("Araç bilgisi alınamadı: " + e.getMessage());
        }
        return "Bilinmeyen " + vehicleType;
    }
    
    public void setPaymentStrategy(Strategy strategy) {
        paymentContext.setPaymentStrategy(strategy);
    }
    
    @Override
    public boolean execute() {
        try {
            // Ödeme formunu göster
            PaymentForm paymentForm = new PaymentForm(null, calculateTotalAmount());
            paymentForm.setVisible(true);

            // Ödeme başarılı değilse işlemi iptal et
            if (!paymentForm.isPaymentSuccessful()) {
                System.out.println("Ödeme iptal edildi.");
                return false;
            }

            // Ödeme işlemini gerçekleştir
            CreditCardPayment payment = paymentForm.getPayment();
            if (!payment.processPayment(calculateTotalAmount())) {
                System.out.println("Ödeme işlemi başarısız oldu.");
                return false;
            }

            // Rezervasyon işlemini gerçekleştir
            boolean success = false;
            if (vehicleType.equalsIgnoreCase("otobüs")) {
                success = ConnectDB.makeReservation(vehicleId, seatNumber, userId);
            } else if (vehicleType.equalsIgnoreCase("uçak")) {
                success = ConnectDB.makeFlightReservation(vehicleId, seatNumber, userId);
            } else {
                throw new IllegalArgumentException("Geçersiz araç türü: " + vehicleType);
            }

            if (success) {
                System.out.println("Rezervasyon başarıyla tamamlandı.");
                System.out.println("Ödeme bilgileri:");
                System.out.println("Ödeme Yöntemi: " + payment.getPaymentMethod());
                System.out.println("Toplam Tutar: " + calculateTotalAmount() + " TL");
                logService.log(
                    String.format("Rezervasyon oluşturuldu - Kullanıcı: %s, %s, Koltuk: %d, Sefer: %s",
                        username, vehicleType, seatNumber, vehicleInfo),
                    LogLevel.SUCCESS
                );
            } else {
                logService.log(
                    String.format("Rezervasyon oluşturulamadı - Kullanıcı: %s, %s, Koltuk: %d, Sefer: %s",
                        username, vehicleType, seatNumber, vehicleInfo),
                    LogLevel.ERROR
                );
            }
            
            executed = success;
            return success;
        } catch (Exception e) {
            System.out.println("Rezervasyon hatası: " + e.getMessage());
            return false;
        }
    }
    
    private double calculateTotalAmount() {
        // Temel fiyat üzerine araç türüne göre ek ücret
        double multiplier = vehicleType.equalsIgnoreCase("uçak") ? 2.0 : 1.0;
        return BASE_PRICE * multiplier;
    }

    @Override
    public void undo() {
        if (executed) {
            // İade onay dialogu
            JPanel panel = new JPanel(new GridLayout(0, 1, 10, 10));
            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            // Bilgi etiketleri
            panel.add(new JLabel("Rezervasyon İptal Bilgileri:"));
            panel.add(new JLabel("Sefer: " + vehicleInfo));
            panel.add(new JLabel("Koltuk: " + seatNumber));
            panel.add(new JLabel(String.format("İade Tutarı: %.2f TL", calculateTotalAmount())));
            panel.add(new JLabel("Not: Rezervasyon iptal edilecek ve iade yapılacaktır."));
            
            int result = JOptionPane.showConfirmDialog(
                null,
                panel,
                "Rezervasyon İptal Onayı",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            
            if (result != JOptionPane.YES_OPTION) {
                logService.log(
                    String.format("Rezervasyon iptali kullanıcı tarafından reddedildi - Kullanıcı: %s, %s, Koltuk: %d, Sefer: %s",
                        username, vehicleType, seatNumber, vehicleInfo),
                    LogLevel.INFO
                );
                return;
            }

            try {
                boolean success;
                if (vehicleType.equalsIgnoreCase("otobüs")) {
                    success = ConnectDB.cancelReservation(vehicleId, seatNumber, userId);
                } else if (vehicleType.equalsIgnoreCase("uçak")) {
                    success = ConnectDB.cancelFlightReservation(vehicleId, seatNumber, userId);
                } else {
                    throw new IllegalArgumentException("Geçersiz araç türü: " + vehicleType);
                }

                if (success) {
                    double refundAmount = calculateTotalAmount();
                    logService.log(
                        String.format("Rezervasyon iptal edildi - Kullanıcı: %s, %s, Koltuk: %d, Sefer: %s, İade Tutarı: %.2f TL",
                            username, vehicleType, seatNumber, vehicleInfo, refundAmount),
                        LogLevel.SUCCESS
                    );
                    
                    // İade başarılı mesajı
                    JOptionPane.showMessageDialog(
                        null,
                        String.format("Rezervasyon iptal edildi.\nİade tutarı (%.2f TL) kartınıza yansıtılacaktır.", refundAmount),
                        "İptal Başarılı",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                    
                    executed = false;
                } else {
                    logService.log(
                        String.format("Rezervasyon iptal edilemedi - Kullanıcı: %s, %s, Koltuk: %d, Sefer: %s",
                            username, vehicleType, seatNumber, vehicleInfo),
                        LogLevel.ERROR
                    );
                    
                    JOptionPane.showMessageDialog(
                        null,
                        "Rezervasyon iptal edilemedi. Lütfen daha sonra tekrar deneyin.",
                        "İptal Hatası",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            } catch (Exception e) {
                logService.log(
                    String.format("Rezervasyon iptal hatası - Kullanıcı: %s, %s, Koltuk: %d, Sefer: %s, Hata: %s",
                        username, vehicleType, seatNumber, vehicleInfo, e.getMessage()),
                    LogLevel.ERROR
                );
                
                JOptionPane.showMessageDialog(
                    null,
                    "Rezervasyon iptal edilirken bir hata oluştu: " + e.getMessage(),
                    "İptal Hatası",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }
} 