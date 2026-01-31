import javax.swing.*;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import reservation_commands.ReservationInvoker;

public class BusUserPanel extends JFrame {
    private int userId;
    private JComboBox<String> tripCombo;
    private JPanel seatPanel;
    private java.util.List<Integer> trips = new ArrayList<>();
    private JButton[] seatButtons = new JButton[40];
    private HashMap<Integer, String> reservedSeatMap = new HashMap<>();
    public String user;
    private ReservationInvoker reservationInvoker;

    public BusUserPanel(String username) {
        this.user = username;
        this.userId = ConnectDB.getUserId(username);
        this.reservationInvoker = new ReservationInvoker();

        // Tam ekran yapı
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(false); // Pencereli ekran
        setTitle("Kullanıcı Paneli - " + username);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(Color.DARK_GRAY);
        setLayout(new BorderLayout(20, 20));

        // ÜST PANEL: Başlık + Butonlar + Sefer seçimi

        // Üst panel için ana panel (dikey)
        JPanel upperPanel = new JPanel();
        upperPanel.setLayout(new BoxLayout(upperPanel, BoxLayout.Y_AXIS));
        upperPanel.setBackground(Color.DARK_GRAY);
        upperPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 10, 30));

        // Üst panelde başlık ve butonlar için yatay panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.DARK_GRAY);

        // Başlık label
        JLabel title = new JLabel("🚌 Otobüs Rezervasyon Sistemi", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setForeground(Color.WHITE);

        // Butonlar paneli (sağda)
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
        seatPanel = new JPanel(new GridLayout(10, 5, 15, 15));
        seatPanel.setBackground(Color.DARK_GRAY);
        seatPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        for (int i = 0; i < 40; i++) {
            JButton btn = new JButton(String.valueOf(i + 1));
            btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
            btn.setPreferredSize(new Dimension(60, 40));
            btn.setBackground(new Color(0x2E8B57)); // YEŞİL
            btn.setForeground(Color.WHITE);
            btn.setOpaque(true);
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            btn.setActionCommand(String.valueOf(i + 1));

            int finalI = i;
            btn.addActionListener(e -> handleSeatClick(finalI));

            seatButtons[i] = btn;
        }

        // 2 - boşluk - 2 düzeni
        for (int row = 0; row < 10; row++) {
            seatPanel.add(seatButtons[row * 4]);
            seatPanel.add(seatButtons[row * 4 + 1]);
            seatPanel.add(new JLabel()); // boşluk
            seatPanel.add(seatButtons[row * 4 + 2]);
            seatPanel.add(seatButtons[row * 4 + 3]);
        }

        add(seatPanel, BorderLayout.CENTER);

        // ALT PANEL: Rezervasyon ve iptal
        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.setBackground(Color.DARK_GRAY);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 20, 30));

        JButton reserveBtn = new JButton("✔ Rezervasyon Yap");
        JButton cancelBtn = new JButton("✖ Rezervasyonu İptal Et");
        JButton undoBtn = new JButton("↶ Son İşlemi Geri Al");

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

        undoBtn.addActionListener(e -> {
            reservationInvoker.undoLastCommand();
            loadSeats();
        });

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
                JOptionPane.showMessageDialog(this, "Bu koltuk zaten dolu.");
                return;
            }

            int tripIndex = tripCombo.getSelectedIndex();
            if (tripIndex == -1) return;

            int tripId = trips.get(tripIndex);
            MakeReservationCommand command = new MakeReservationCommand(tripId, selectedSeat, userId, "otobüs");
            boolean success = reservationInvoker.executeCommand(command);
            
            if (success) {
                JOptionPane.showMessageDialog(this, "Rezervasyon başarıyla yapıldı!");
                loadSeats();
            } else {
                JOptionPane.showMessageDialog(this, "Rezervasyon başarısız.");
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
            int tripId = trips.get(tripIndex);
            
            CancelReservationCommand command = new CancelReservationCommand(tripId, selectedSeat, userId, "otobüs");
            boolean success = reservationInvoker.executeCommand(command);
            
            if (success) {
                
                loadSeats();
            } else {
                JOptionPane.showMessageDialog(this, "İptal edilemedi (seçilen koltuk size ait olmayabilir).");
            }
        });

        loadTrips();
        setVisible(true);
    }

    private void handleSeatClick(int index) {
        JButton clicked = seatButtons[index];
        int seatNum = index + 1;

        if (clicked.getBackground().equals(Color.YELLOW)) {
            resetSeatColors();
        } else {
            resetSeatColors();
            clicked.setBackground(Color.YELLOW);
        }
    }

    private void resetSeatColors() {
        for (int i = 0; i < 40; i++) {
            int seatNum = i + 1;
            if (reservedSeatMap.containsKey(seatNum)) {
                if (reservedSeatMap.get(seatNum).equals(user)) {
                    seatButtons[i].setBackground(Color.BLUE);
                } else {
                    seatButtons[i].setBackground(Color.RED);
                }
            } else {
                seatButtons[i].setBackground(new Color(0x2E8B57));
            }
        }
    }

    private int getSelectedSeat() {
        for (int i = 0; i < seatButtons.length; i++) {
            if (seatButtons[i].getBackground().equals(Color.YELLOW)) {
                return i + 1;
            }
        }
        return -1;
    }

    private void loadTrips() {
        try (ResultSet rs = ConnectDB.getTrips()) {
            tripCombo.removeAllItems();
            trips.clear();
            while (rs != null && rs.next()) {
                int id = rs.getInt("trip_id");
                String busName = rs.getString("bus_name");
                String date = rs.getString("trip_date");
                String time = rs.getString("trip_time");
                trips.add(id);
                tripCombo.addItem(busName + " - " + date + " " + time);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadSeats() {
        int selectedIndex = tripCombo.getSelectedIndex();
        if (selectedIndex == -1) return;

        int selectedTrip = trips.get(selectedIndex);
        reservedSeatMap.clear();
        reservedSeatMap = ConnectDB.getReservedSeatsWithUsernames(selectedTrip, reservedSeatMap);
        resetSeatColors();
    }
}
