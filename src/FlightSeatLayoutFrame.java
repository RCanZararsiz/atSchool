import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.*;
import logging.*;

public class FlightSeatLayoutFrame extends JFrame {
    private LogService logService;

    public FlightSeatLayoutFrame(int planeId, String planeName) {
        this.logService = LogService.getInstance();
        
        setTitle(planeName + " - Uçuş & Koltuk Düzeni");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        Color backgroundColor = new Color(43, 43, 43);
        Color buttonColor = new Color(60, 63, 65);
        Color seatColor = new Color(75, 110, 175);
        Color textColor = Color.WHITE;
        Font font = new Font("Segoe UI", Font.PLAIN, 16);

        getContentPane().setBackground(backgroundColor);

        // Üst panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.setBackground(backgroundColor);
        JButton deleteFlightBtn = new JButton("Uçuş Sil");
        deleteFlightBtn.setFont(font);
        deleteFlightBtn.setBackground(buttonColor);
        deleteFlightBtn.setForeground(textColor);
        topPanel.add(deleteFlightBtn);
        add(topPanel, BorderLayout.NORTH);

        // Sol panel - Uçuş listesi
        DefaultListModel<String> flightsModel = new DefaultListModel<>();
        java.util.List<Integer> flightIds = new ArrayList<>();
        JList<String> flightsList = new JList<>(flightsModel);
        flightsList.setFont(font);
        flightsList.setBackground(buttonColor);
        flightsList.setForeground(textColor);
        flightsList.setFixedCellHeight(40);
        JScrollPane flightsScroll = new JScrollPane(flightsList);
        flightsScroll.setPreferredSize(new Dimension(250, 0));
        add(flightsScroll, BorderLayout.WEST);

        // Merkez panel - Koltuklar
        JPanel seatPanel = new JPanel(new GridLayout(30, 7, 5, 5));
        seatPanel.setBackground(backgroundColor);
        JScrollPane seatScroll = new JScrollPane(seatPanel);
        seatScroll.getViewport().setBackground(backgroundColor);
        add(seatScroll, BorderLayout.CENTER);

        // Uçuşları veritabanından çek
        try {
            ResultSet rs = ConnectDB.getFlightsByPlane(planeId);
            while (rs.next()) {
                int fid = rs.getInt("flight_id");
                String date = rs.getString("flight_date");
                String time = rs.getString("flight_time");
                flightsModel.addElement("Uçuş #" + fid + " — " + date + " " + time);
                flightIds.add(fid);
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Uçuş seçilince koltukları çiz
        flightsList.addListSelectionListener(ev -> {
            if (!ev.getValueIsAdjusting() && flightsList.getSelectedIndex() != -1) {
                seatPanel.removeAll();
                int selectedFlightId = flightIds.get(flightsList.getSelectedIndex());

                Map<Integer, String> reserved = ConnectDB.getReservedSeatsForFlight(selectedFlightId);

                for (int row = 0; row < 30; row++) {
                    for (int col = 0; col < 7; col++) {
                        if (col == 3) {
                            seatPanel.add(new JLabel()); // koridor boşluğu
                        } else {
                            int seatNum = row * 6 + (col < 3 ? col : col - 1) + 1;
                            JButton btn = new JButton(String.valueOf(seatNum));
                            btn.setFont(font);
                            btn.setBackground(seatColor);
                            btn.setForeground(textColor);

                            if (reserved.containsKey(seatNum)) {
                                btn.setText(seatNum + " - " + reserved.get(seatNum));
                                btn.setEnabled(false);
                                btn.setBackground(Color.RED);
                                btn.setForeground(Color.WHITE);
                            }

                            seatPanel.add(btn);
                        }
                    }
                }

                seatPanel.revalidate();
                seatPanel.repaint();
            }
        });

        // Uçuş silme işlemi
        deleteFlightBtn.addActionListener(e -> {
            int selectedIndex = flightsList.getSelectedIndex();
            if (selectedIndex == -1) {
                JOptionPane.showMessageDialog(this, "Lütfen silmek için bir uçuş seçin.");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this, "Bu uçuş silinecek. Emin misiniz?", "Uçuş Sil", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                int selectedFlightId = flightIds.get(selectedIndex);
                String selectedFlightInfo = flightsList.getSelectedValue();
                
                if (ConnectDB.deleteFlight(selectedFlightId)) {
                    logService.log("Uçuş silindi: " + planeName + " - " + selectedFlightInfo, LogLevel.SUCCESS);
                    JOptionPane.showMessageDialog(this, "Uçuş başarıyla silindi.");
                    flightsModel.remove(selectedIndex);
                    flightIds.remove(selectedIndex);
                    seatPanel.removeAll();
                    seatPanel.revalidate();
                    seatPanel.repaint();
                } else {
                    logService.log("Uçuş silinirken hata oluştu: " + planeName + " - " + selectedFlightInfo, LogLevel.ERROR);
                    JOptionPane.showMessageDialog(this, "Uçuş silinemedi.");
                }
            }
        });

        setVisible(true);
    }
}
