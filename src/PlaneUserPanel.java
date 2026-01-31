import javax.swing.*;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import reservation_commands.ReservationInvoker;

public class PlaneUserPanel extends JFrame {
    private int userId;
    private JComboBox<String> tripCombo;
    private JPanel seatPanel;
    private java.util.List<Integer> trips = new ArrayList<>();
    private JButton[] seatButtons = new JButton[180];
    private HashMap<Integer, String> reservedSeatMap = new HashMap<>();
    public String user;
    private ReservationInvoker reservationInvoker;

    public PlaneUserPanel(String username) {
        user = username;
        this.userId = ConnectDB.getUserId(username);
        this.reservationInvoker = new ReservationInvoker();

        // Tam ekran yapı
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(false);
        setTitle("Uçak Kullanıcı Paneli - " + username);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(Color.DARK_GRAY);
        setLayout(new BorderLayout(20, 20));

        Color greenSeat = new Color(0x2E8B57);
        Color redSeat = Color.RED;
        Color blueSeat = Color.BLUE;
        Color yellowSeat = new Color(218, 165, 32);

        JPanel upperPanel = new JPanel();
        upperPanel.setLayout(new BoxLayout(upperPanel, BoxLayout.Y_AXIS));
        upperPanel.setBackground(Color.DARK_GRAY);
        upperPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 10, 30));


        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.DARK_GRAY);

        JLabel title = new JLabel("✈️ Uçak Rezervasyon Sistemi", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setForeground(Color.WHITE);

        JPanel topButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        topButtonPanel.setBackground(Color.DARK_GRAY);

        JButton backToLoginBtn = new JButton("🔙 Giriş Ekranı");
        JButton backToChoosingBtn = new JButton("🧭 Seçim Ekranı");

        Font btnFont = new Font("Segoe UI", Font.BOLD, 14);
        backToLoginBtn.setFont(btnFont);
        backToChoosingBtn.setFont(btnFont);

        backToLoginBtn.setBackground(Color.GRAY);
        backToChoosingBtn.setBackground(Color.GRAY);
        backToLoginBtn.setForeground(Color.WHITE);
        backToChoosingBtn.setForeground(Color.WHITE);
        backToLoginBtn.setFocusPainted(false);
        backToChoosingBtn.setFocusPainted(false);

        topButtonPanel.add(backToLoginBtn);
        topButtonPanel.add(backToChoosingBtn);

        // BorderLayout'a ekle
        topPanel.add(title, BorderLayout.CENTER);
        topPanel.add(topButtonPanel, BorderLayout.EAST);

        // Sefer seçimi paneli
        JPanel tripSelectPanel = new JPanel();
        tripSelectPanel.setBackground(Color.DARK_GRAY);
        JLabel selectLabel = new JLabel("Sefer Seçiniz: ");
        selectLabel.setForeground(Color.WHITE);
        selectLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));

        tripCombo = new JComboBox<>();
        tripCombo.setPreferredSize(new Dimension(400, 30));
        tripCombo.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        tripCombo.setBackground(Color.LIGHT_GRAY);

        tripSelectPanel.add(selectLabel);
        tripSelectPanel.add(tripCombo);
        tripSelectPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        // Üst panelde başlık + butonlar ve sefer seçimi panelini dikey sırayla ekle
        upperPanel.add(topPanel);
        upperPanel.add(tripSelectPanel);

        add(upperPanel, BorderLayout.NORTH);

        // KOLTUK PANELİ
        seatPanel = new JPanel(new GridLayout(30, 7, 5, 5));
        seatPanel.setBackground(Color.DARK_GRAY);
        seatPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Koltuk butonları
        for (int i = 0; i < 180; i++) {
            JButton btn = new JButton(String.valueOf(i + 1));
            btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btn.setPreferredSize(new Dimension(40, 30));
            btn.setBackground(greenSeat);
            btn.setForeground(Color.WHITE);
            btn.setOpaque(true);
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            btn.setMargin(new Insets(1, 1, 1, 1));
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.setActionCommand(String.valueOf(i + 1));
            
            btn.addActionListener(e -> {
                JButton clicked = (JButton) e.getSource();
                int seatNum = Integer.parseInt(clicked.getActionCommand());
                if (clicked.getBackground().equals(yellowSeat)) {
                    if (!reservedSeatMap.containsKey(seatNum)) {
                        clicked.setBackground(greenSeat);
                    } else if (reservedSeatMap.get(seatNum).equals(user)) {
                        clicked.setBackground(blueSeat);
                    } else {
                        clicked.setBackground(redSeat);
                    }
                } else {
                    for (JButton b : seatButtons) {
                        int sn = Integer.parseInt(b.getActionCommand());
                        if (b.getBackground().equals(yellowSeat)) {
                            if (!reservedSeatMap.containsKey(sn)) {
                                b.setBackground(greenSeat);
                            } else if (reservedSeatMap.get(sn).equals(user)) {
                                b.setBackground(blueSeat);
                            } else {
                                b.setBackground(redSeat);
                            }
                        }
                    }
                    clicked.setBackground(yellowSeat);
                }
            });
            seatButtons[i] = btn;
        }

        // 3 - boşluk - 3 düzeni (uçak koltuk düzeni)
        for (int row = 0; row < 30; row++) {
            seatPanel.add(seatButtons[row * 6]);
            seatPanel.add(seatButtons[row * 6 + 1]);
            seatPanel.add(seatButtons[row * 6 + 2]);
            seatPanel.add(new JLabel("")); // koridor boşluğu
            seatPanel.add(seatButtons[row * 6 + 3]);
            seatPanel.add(seatButtons[row * 6 + 4]);
            seatPanel.add(seatButtons[row * 6 + 5]);
        }

        add(seatPanel, BorderLayout.CENTER);

        // ALT PANEL: Rezervasyon ve iptal
        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.setBackground(Color.DARK_GRAY);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 20, 30));

        JButton reserveBtn = new JButton("✔ Rezervasyon Yap");
        JButton cancelBtn = new JButton("✖ Rezervasyonu İptal Et");
        JButton undoBtn = new JButton("↶ Son İşlemi Geri Al");

        // Buton stilleri
        reserveBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        cancelBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        undoBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));

        reserveBtn.setBackground(new Color(0x1E90FF));
        cancelBtn.setBackground(new Color(0xDC143C));
        undoBtn.setBackground(new Color(0x9C27B0));

        reserveBtn.setForeground(Color.WHITE);
        cancelBtn.setForeground(Color.WHITE);
        undoBtn.setForeground(Color.WHITE);

        reserveBtn.setFocusPainted(false);
        cancelBtn.setFocusPainted(false);
        undoBtn.setFocusPainted(false);

        bottomPanel.add(reserveBtn);
        bottomPanel.add(cancelBtn);
        bottomPanel.add(undoBtn);

        add(bottomPanel, BorderLayout.SOUTH);

        // Buton aksiyonları
        backToLoginBtn.addActionListener(e -> {
            this.dispose();
            new SignIn().setVisible(true);
        });

        backToChoosingBtn.addActionListener(e -> {
            this.dispose();
            new ChoosingScreen(username).setVisible(true);
        });

        tripCombo.addActionListener(e -> loadSeats());

        reserveBtn.addActionListener(e -> {
            int selectedSeat = getSelectedSeat();
            if (selectedSeat == -1) {
                JOptionPane.showMessageDialog(this, "Lütfen bir koltuk seçin.");
                return;
            }
            if (reservedSeatMap.containsKey(selectedSeat)) {
                JOptionPane.showMessageDialog(this, "Bu koltuk zaten dolu!");
                return;
            }
            int tripIndex = tripCombo.getSelectedIndex();
            if (tripIndex == -1) return;
            Integer selectedTrip = trips.get(tripIndex);
            
            MakeReservationCommand command = new MakeReservationCommand(selectedTrip, selectedSeat, userId, "uçak");
            boolean success = reservationInvoker.executeCommand(command);
            
            if (success) {
                JOptionPane.showMessageDialog(this, "Rezervasyon başarılı.");
                loadSeats();
            } else {
                JOptionPane.showMessageDialog(this, "Bu koltuk dolu veya hata oluştu.");
            }
        });

        cancelBtn.addActionListener(e -> {
            int selectedSeat = getSelectedSeat();
            if (selectedSeat == -1) {
                JOptionPane.showMessageDialog(this, "Lütfen iptal etmek için bir koltuk seçin.");
                return;
            }
            if (!reservedSeatMap.containsKey(selectedSeat)) {
                JOptionPane.showMessageDialog(this, "Seçilen koltuk dolu değil, iptal edilemez.");
                return;
            }
            int tripIndex = tripCombo.getSelectedIndex();
            if (tripIndex == -1) return;
            Integer selectedTrip = trips.get(tripIndex);
            
            CancelReservationCommand command = new CancelReservationCommand(selectedTrip, selectedSeat, userId, "uçak");
            boolean success = reservationInvoker.executeCommand(command);
            
            if (success) {
               
                loadSeats();
            } else {
                JOptionPane.showMessageDialog(this, "İptal edilemedi (seçilen koltuk size ait olmayabilir).");
            }
        });

        undoBtn.addActionListener(e -> {
            reservationInvoker.undoLastCommand();
            loadSeats();
        });

        loadTrips();
        setVisible(true);
    }

    private int getSelectedSeat() {
        for (JButton btn : seatButtons) {
            if (btn.getBackground().equals(new Color(218, 165, 32))) {
                return Integer.parseInt(btn.getActionCommand());
            }
        }
        return -1;
    }

    private void loadTrips() {
        try (ResultSet rs = ConnectDB.getFlights()) {
            tripCombo.removeAllItems();
            trips.clear();
            while (rs != null && rs.next()) {
                int id = rs.getInt("flight_id");
                String planeName = rs.getString("plane_name");
                String date = rs.getString("flight_date");
                String time = rs.getString("flight_time");
                trips.add(id);
                tripCombo.addItem(planeName + " - " + date + " " + time);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadSeats() {
        int selectedIndex = tripCombo.getSelectedIndex();
        if (selectedIndex == -1) return;
        Integer selectedTrip = trips.get(selectedIndex);
        for (JButton btn : seatButtons) {
            btn.setBackground(new Color(0x2E8B57));
        }
        reservedSeatMap.clear();
        reservedSeatMap = ConnectDB.getReservedSeatsWithUsernamesForFlight(selectedTrip, reservedSeatMap);

        for (int seatNum : reservedSeatMap.keySet()) {
            if (seatNum >= 1 && seatNum <= seatButtons.length) {
                if (reservedSeatMap.get(seatNum).equals(user)) {
                    seatButtons[seatNum - 1].setBackground(new Color(65, 105, 225));
                } else {
                    seatButtons[seatNum - 1].setBackground(new Color(178, 34, 34));
                }
            }
        }
    }
}