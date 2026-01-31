import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.*;
import logging.*;

public class BusSeatLayoutFrame extends JFrame {
    private LogService logService;

    public BusSeatLayoutFrame(int busId, String busName) {
        this.logService = LogService.getInstance();
        
        setTitle(busName + " - Sefer & Koltuk Düzeni");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Tam ekran
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // RENK AYARLARI
        Color backgroundColor = new Color(40, 44, 52); // koyu füme
        Color seatFreeColor = new Color(100, 149, 237); // steel blue
        Color seatTakenColor = new Color(178, 34, 34); // firebrick
        Color textColor = Color.WHITE;
        Font font = new Font("Segoe UI", Font.BOLD, 18);

        // ÜST PANEL
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.setBackground(backgroundColor);
        JButton deleteTripBtn = new JButton("Seferi Sil");
        deleteTripBtn.setFocusPainted(false);
        deleteTripBtn.setBackground(new Color(220, 20, 60));
        deleteTripBtn.setForeground(Color.WHITE);
        deleteTripBtn.setFont(font);
        topPanel.add(deleteTripBtn);
        add(topPanel, BorderLayout.NORTH);

        // SOL PANEL (Sefer Listesi)
        DefaultListModel<String> tripsModel = new DefaultListModel<>();
        java.util.List<Integer> tripIds = new ArrayList<>();
        try (ResultSet rs = ConnectDB.getTripsByBus(busId)) {
            while (rs.next()) {
                int tid = rs.getInt("trip_id");
                String date = rs.getString("trip_date");
                String time = rs.getString("trip_time");
                tripsModel.addElement("Sefer #" + tid + "—" + date + " " + time);
                tripIds.add(tid);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        JList<String> tripsList = new JList<>(tripsModel);
        tripsList.setFont(font);
        tripsList.setBackground(new Color(60, 63, 72));
        tripsList.setForeground(textColor);
        JScrollPane tripsScroll = new JScrollPane(tripsList);
        tripsScroll.setPreferredSize(new Dimension(250, 0));
        add(tripsScroll, BorderLayout.WEST);

        // KOLTUK PANELİ
        JPanel seatPanel = new JPanel(new GridLayout(10, 5, 10, 10));
        seatPanel.setBackground(backgroundColor);
        add(seatPanel, BorderLayout.CENTER);

        // Sefer seçilince koltukları çiz
        tripsList.addListSelectionListener(ev -> {
            if (!ev.getValueIsAdjusting() && tripsList.getSelectedIndex() != -1) {
                seatPanel.removeAll();
                int selectedTripId = tripIds.get(tripsList.getSelectedIndex());

                Map<Integer, String> reserved = ConnectDB.getReservedSeatsWithUser(selectedTripId);

                for (int row = 0; row < 10; row++) {
                    for (int col = 0; col < 5; col++) {
                        if (col == 2) {
                            seatPanel.add(Box.createGlue()); // koridor
                        } else {
                            int seatNum = row * 4 + (col < 2 ? col : col - 1) + 1;
                            JButton seatBtn = new JButton(String.valueOf(seatNum));
                            seatBtn.setFocusPainted(false);
                            seatBtn.setFont(font);
                            seatBtn.setOpaque(true);
                            seatBtn.setBorderPainted(false);
                            seatBtn.setForeground(textColor);

                            if (reserved.containsKey(seatNum)) {
                                seatBtn.setText(seatNum + " - " + reserved.get(seatNum));
                                seatBtn.setEnabled(false);
                                seatBtn.setBackground(seatTakenColor);
                            } else {
                                seatBtn.setBackground(seatFreeColor);
                            }

                            seatPanel.add(seatBtn);
                        }
                    }
                }

                seatPanel.revalidate();
                seatPanel.repaint();
            }
        });

        // Sefer silme işlemi
        deleteTripBtn.addActionListener(e -> {
            int selectedIndex = tripsList.getSelectedIndex();
            if (selectedIndex == -1) {
                JOptionPane.showMessageDialog(this, "Lütfen silmek için bir sefer seçin.");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this, "Bu sefer silinecek. Emin misiniz?", "Sefer Sil", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                int selectedTripId = tripIds.get(selectedIndex);
                String selectedTripInfo = tripsList.getSelectedValue();
                
                if (ConnectDB.deleteTrip(selectedTripId)) {
                    logService.log("Sefer silindi: " + busName + " - " + selectedTripInfo, LogLevel.SUCCESS);
                    JOptionPane.showMessageDialog(this, "Sefer başarıyla silindi.");
                    tripsModel.remove(selectedIndex);
                    tripIds.remove(selectedIndex);
                    seatPanel.removeAll();
                    seatPanel.revalidate();
                    seatPanel.repaint();
                } else {
                    logService.log("Sefer silinirken hata oluştu: " + busName + " - " + selectedTripInfo, LogLevel.ERROR);
                    JOptionPane.showMessageDialog(this, "Sefer silinemedi.");
                }
            }
        });

        setVisible(true);
    }
}
