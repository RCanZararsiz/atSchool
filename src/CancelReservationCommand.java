import reservation_commands.Command;
import logging.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.*;
import java.awt.*;

public class CancelReservationCommand implements Command {
    private int vehicleId;
    private int seatNumber;
    private int userId;
    private String vehicleType;
    private LogService logService;
    private String username;
    private String vehicleInfo;
    private static final double REFUND_RATE = 1.0; // İade oranı %100
    
    public CancelReservationCommand(int vehicleId, int seatNumber, int userId, String vehicleType) {
        this.vehicleId = vehicleId;
        this.seatNumber = seatNumber;
        this.userId = userId;
        this.vehicleType = vehicleType;
        this.logService = LogService.getInstance();
        this.username = ConnectDB.getUsername(userId);
        this.vehicleInfo = getVehicleInfo();
    }
    
    private String getVehicleInfo() {
        try {
            if (vehicleType.equalsIgnoreCase("otobüs")) {
                ResultSet rs = ConnectDB.getTripInfo(vehicleId);
                if (rs != null && rs.next()) {
                    String busName = rs.getString("bus_name");
                    String date = rs.getString("trip_date");
                    String time = rs.getString("trip_time");
                    return busName + " - " + date + " " + time;
                }
            } else {
                ResultSet rs = ConnectDB.getFlightInfo(vehicleId);
                if (rs != null && rs.next()) {
                    String planeName = rs.getString("plane_name");
                    String date = rs.getString("flight_date");
                    String time = rs.getString("flight_time");
                    return planeName + " - " + date + " " + time;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Bilinmeyen " + vehicleType;
    }

    private double calculateRefundAmount() {
        // Temel fiyat (MakeReservationCommand ile aynı)
        double basePrice = 150.0;
        // İade tutarını hesapla
        return basePrice * REFUND_RATE;
    }
    
    private boolean showRefundConfirmationDialog() {
        double refundAmount = calculateRefundAmount();
        
        // İade onay dialogu
        JPanel panel = new JPanel(new GridLayout(0, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Bilgi etiketleri
        panel.add(new JLabel("Rezervasyon İptal Bilgileri:"));
        panel.add(new JLabel("Sefer: " + vehicleInfo));
        panel.add(new JLabel("Koltuk: " + seatNumber));
        panel.add(new JLabel(String.format("İade Tutarı: %.2f TL", refundAmount)));
        panel.add(new JLabel("Not: İptal işlemi geri alınamaz."));
        
        // Onay butonları
        int result = JOptionPane.showConfirmDialog(
            null,
            panel,
            "Rezervasyon İptal Onayı",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        return result == JOptionPane.YES_OPTION;
    }
    
    @Override
    public boolean execute() {
        // İade onayını al
       

        boolean success;
        if (vehicleType.equalsIgnoreCase("otobüs")) {
            success = ConnectDB.cancelReservation(vehicleId, seatNumber, userId);
        } else {
            success = ConnectDB.cancelFlightReservation(vehicleId, seatNumber, userId);
        }
        
        if (success) {
        	double refundAmount = calculateRefundAmount();
            if (!showRefundConfirmationDialog()) {
                logService.log(
                    String.format("Rezervasyon iptali kullanıcı tarafından reddedildi - Kullanıcı: %s, %s, Koltuk: %d, Sefer: %s",
                        username, vehicleType, seatNumber, vehicleInfo),
                    LogLevel.INFO
                );}
                else {
                	 
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
        } }else {
        	
            logService.log(
                String.format("Rezervasyon iptal edilemedi - Kullanıcı: %s, %s, Koltuk: %d, Sefer: %s",
                    username, vehicleType, seatNumber, vehicleInfo),
                LogLevel.ERROR
            );
            
            
       
            return false;
        }
        
        return success;
    }
    
    @Override
    public void undo() {
        // İptal işlemi geri alınamaz
        JOptionPane.showMessageDialog(
            null,
            "Rezervasyon iptali geri alınamaz.",
            "İşlem Geri Alınamaz",
            JOptionPane.WARNING_MESSAGE
        );
    }
} 