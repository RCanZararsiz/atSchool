import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import logging.*;

public class BusAdminPanel extends JFrame implements Observer {
    private JPanel busListPanel;
    private JComboBox<String> busCombo;
    private java.util.List<Integer> busIds = new ArrayList<>();
    private LogService logService;
    private JButton viewLogsButton;

    public BusAdminPanel(String user) {
        this.logService = LogService.getInstance();
        logService.attach(this);
        
        setTitle("Admin Paneli - Otobüsler & Seferler");
        setExtendedState(JFrame.MAXIMIZED_BOTH);  // Tam ekran
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Koyu tema renkleri
        Color backgroundColor = new Color(30, 30, 30);
        Color panelColor = new Color(40, 40, 40);
        Color textColor = Color.WHITE;

        getContentPane().setBackground(backgroundColor);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(backgroundColor);
        setContentPane(mainPanel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Kenar boşlukları
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        // Üst panel - Otobüs ekleme + SignIn ve Choosing butonları
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(panelColor);

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        inputPanel.setBackground(panelColor);

        JLabel lbl = new JLabel("Otobüs İsmi:");
        lbl.setForeground(textColor);
        JTextField busField = new JTextField(20);
        JButton addBusBtn = createStyledButton("Otobüs Ekle", new Color(70, 70, 70), textColor);

        inputPanel.add(lbl);
        inputPanel.add(busField);
        inputPanel.add(addBusBtn);

        topPanel.add(inputPanel, BorderLayout.WEST);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        buttonPanel.setBackground(panelColor);

        viewLogsButton = createStyledButton("Logları Görüntüle", new Color(70, 70, 70), textColor);
        JButton btnSignIn = createStyledButton("Giriş Ekranı", new Color(70, 70, 70), textColor);
        JButton btnChoosingScreen = createStyledButton("Seçim Ekranı", new Color(70, 70, 70), textColor);

        buttonPanel.add(viewLogsButton);
        buttonPanel.add(btnSignIn);
        buttonPanel.add(btnChoosingScreen);

        topPanel.add(buttonPanel, BorderLayout.EAST);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(topPanel, gbc);

        // Orta panel - Sefer ekleme
        JPanel middlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        middlePanel.setBackground(panelColor);

        busCombo = new JComboBox<>();
        busCombo.setPreferredSize(new Dimension(180, 30));

        JTextField tripDateField = new JTextField(12);
        JTextField tripTimeField = new JTextField(6);
        tripDateField.setPreferredSize(new Dimension(120, 30));
        tripTimeField.setPreferredSize(new Dimension(80, 30));

        addPlaceholderBehavior(tripDateField, "YYYY-MM-DD");
        addPlaceholderBehavior(tripTimeField, "HH:MM");

        JLabel lblBus = new JLabel("Otobüs Seç:");
        JLabel lblDate = new JLabel("Tarih:");
        JLabel lblTime = new JLabel("Saat:");
        lblBus.setForeground(textColor);
        lblDate.setForeground(textColor);
        lblTime.setForeground(textColor);

        JButton addTripBtn = createStyledButton("Sefer Ekle", new Color(70, 70, 70), textColor);

        middlePanel.add(lblBus);
        middlePanel.add(busCombo);
        middlePanel.add(lblDate);
        middlePanel.add(tripDateField);
        middlePanel.add(lblTime);
        middlePanel.add(tripTimeField);
        middlePanel.add(addTripBtn);

        gbc.gridy = 1;
        mainPanel.add(middlePanel, gbc);

        // Alt panel - Otobüs butonları listesi (scroll), ortalanmış şekilde
        busListPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        busListPanel.setBackground(panelColor);

        JScrollPane scrollPane = new JScrollPane(busListPanel);
        scrollPane.setPreferredSize(new Dimension(1000, 300));  // Geniş bir scroll alanı
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        gbc.gridy = 2;
        gbc.weighty = 1; // Alt panel büyüyebilir
        gbc.fill = GridBagConstraints.BOTH; // Dikey ve yatay büyüme
        mainPanel.add(scrollPane, gbc);

        // Buton aksiyonları ve liste yenileme aynı

        addBusBtn.addActionListener(e -> {
            String busName = busField.getText().trim();
            if (!busName.isEmpty()) {
                if (ConnectDB.addBus(busName)) {
                    logService.log("Yeni otobüs eklendi: " + busName, LogLevel.SUCCESS);
                    busField.setText("");
                    refreshBusList();
                    loadBusesToCombo();
                } else {
                    logService.log("Otobüs eklenirken hata oluştu: " + busName, LogLevel.ERROR);
                }
            }
        });

        addTripBtn.addActionListener(e -> {
            String date = tripDateField.getText().trim();
            String time = tripTimeField.getText().trim();
            int selectedIndex = busCombo.getSelectedIndex();
            
            if (selectedIndex >= 0 && !date.equals("YYYY-MM-DD") && !time.equals("HH:MM")) {
                int busId = busIds.get(selectedIndex);
                String busName = busCombo.getSelectedItem().toString();
                
                if (ConnectDB.addTrip(busId, date, time)) {
                    logService.log("Yeni sefer eklendi: " + busName + " - " + date + " " + time, LogLevel.SUCCESS);
                    tripDateField.setText("YYYY-MM-DD");
                    tripTimeField.setText("HH:MM");
                } else {
                    logService.log("Sefer eklenirken hata oluştu: " + busName + " - " + date + " " + time, LogLevel.ERROR);
                }
            }
        });

        // Yeni butonların actionları
        btnSignIn.addActionListener(e -> {
            new SignIn(); // Kendi SignIn ekranın, parametre varsa ekle
            dispose();
        });

        btnChoosingScreen.addActionListener(e -> {
            new ChoosingScreen(user); // Kendi ChoosingScreen ekranın, parametre varsa ekle
            dispose();
        });

        // Log görüntüleme butonu aksiyonu
        viewLogsButton.addActionListener(e -> showLogs());

        loadBusesToCombo();
        refreshBusList();

        setVisible(true);
    }

    private JButton createStyledButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(140, 40));
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(fgColor));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void addPlaceholderBehavior(JTextField field, String placeholder) {
        field.setText(placeholder);
        field.setForeground(Color.GRAY);
        field.setBackground(new Color(50, 50, 50));
        field.setCaretColor(Color.WHITE);

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.WHITE);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(Color.GRAY);
                }
            }
        });
    }

    private void loadBusesToCombo() {
        busCombo.removeAllItems();
        busIds.clear();

        try (ResultSet rs = ConnectDB.getBuses()) {
            while (rs != null && rs.next()) {
                int id = rs.getInt("bus_id");
                String name = rs.getString("bus_name");
                busCombo.addItem(name);
                busIds.add(id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void refreshBusList() {
        busListPanel.removeAll();

        try (ResultSet rs = ConnectDB.getBuses()) {
            while (rs != null && rs.next()) {
                int busId = rs.getInt("bus_id");
                String busName = rs.getString("bus_name");

                JPanel container = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
                container.setBackground(new Color(40, 40, 40));

                JButton busBtn = createStyledButton(busName, new Color(70, 70, 70), Color.WHITE);
                busBtn.setPreferredSize(new Dimension(200, 40));
                busBtn.addActionListener(e -> new BusSeatLayoutFrame(busId, busName));

                JButton deleteBtn = createStyledButton("Sil", new Color(150, 50, 50), Color.WHITE);
                deleteBtn.setPreferredSize(new Dimension(60, 40));
                deleteBtn.addActionListener(e -> {
                    int confirm = JOptionPane.showConfirmDialog(this, busName + " silinsin mi?", "Onay", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION && ConnectDB.deleteBus(busId)) {
                        JOptionPane.showMessageDialog(this, "Otobüs silindi.");
                        refreshBusList();
                        loadBusesToCombo();
                    }
                });

                container.add(busBtn);
                container.add(deleteBtn);
                busListPanel.add(container);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        busListPanel.revalidate();
        busListPanel.repaint();
    }

    private void showLogs() {
        JDialog logDialog = new JDialog(this, "Sistem Logları", true);
        logDialog.setSize(800, 600);
        logDialog.setLocationRelativeTo(this);

        JPanel logPanel = new JPanel(new BorderLayout());
        JTextArea logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(logArea);
        logPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JComboBox<LogLevel> levelFilter = new JComboBox<>(LogLevel.values());
        JButton filterButton = new JButton("Filtrele");
        JButton clearButton = new JButton("Temizle");

        filterPanel.add(new JLabel("Log Seviyesi:"));
        filterPanel.add(levelFilter);
        filterPanel.add(filterButton);
        filterPanel.add(clearButton);

        logPanel.add(filterPanel, BorderLayout.NORTH);

        filterButton.addActionListener(e -> {
            LogLevel selectedLevel = (LogLevel) levelFilter.getSelectedItem();
            updateLogDisplay(logArea, selectedLevel);
        });

        clearButton.addActionListener(e -> {
            logService.clearLogs();
            updateLogDisplay(logArea, null);
        });

        updateLogDisplay(logArea, null);

        logDialog.add(logPanel);
        logDialog.setVisible(true);
    }

    private void updateLogDisplay(JTextArea logArea, LogLevel filterLevel) {
        logArea.setText("");
        java.util.List<LogRecord> logs = filterLevel == null ? 
            logService.getLogs() : 
            logService.getLogsByLevel(filterLevel);

        for (LogRecord log : logs) {
            logArea.append(log.toString() + "\n");
        }
    }

    @Override
    public void update(String message, LogLevel level) {
        // Log güncellemelerini dinle ve gerekirse UI'ı güncelle
        SwingUtilities.invokeLater(() -> {
            // Örneğin, log sayısını gösteren bir label varsa güncellenebilir
        });
    }
}
