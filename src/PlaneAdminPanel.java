import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import logging.*;

public class PlaneAdminPanel extends JFrame implements Observer {
    private JPanel planeListPanel;
    private JComboBox<String> planeCombo;
    private java.util.List<Integer> planeIds = new ArrayList<>();
    private LogService logService;

    public PlaneAdminPanel(String user) {
        this.logService = LogService.getInstance();
        logService.attach(this);
        
        setTitle("Admin Paneli - Uçaklar & Seferler");
        setExtendedState(JFrame.MAXIMIZED_BOTH);  // Tam ekran
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Koyu tema renkleri
        Color backgroundColor = new Color(30, 30, 30);
        Color panelColor = new Color(40, 40, 40);
        Color textColor = Color.WHITE;

        getContentPane().setBackground(backgroundColor);

        // Ana panel GridBagLayout ile
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(backgroundColor);
        setContentPane(mainPanel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        // Üst panel - Uçak ekleme + SignIn ve Choosing butonları
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(panelColor);

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        inputPanel.setBackground(panelColor);

        JLabel lbl = new JLabel("Uçak İsmi:");
        lbl.setForeground(textColor);
        JTextField planeField = new JTextField(20);
        JButton addPlaneBtn = createStyledButton("Uçak Ekle", new Color(70, 70, 70), textColor);

        inputPanel.add(lbl);
        inputPanel.add(planeField);
        inputPanel.add(addPlaneBtn);

        topPanel.add(inputPanel, BorderLayout.WEST);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        buttonPanel.setBackground(panelColor);

        JButton btnViewLogs = createStyledButton("Logları Görüntüle", new Color(70, 70, 70), textColor);
        JButton btnSignIn = createStyledButton("Giriş Ekranı", new Color(70, 70, 70), textColor);
        JButton btnChoosingScreen = createStyledButton("Seçim Ekranı", new Color(70, 70, 70), textColor);

        buttonPanel.add(btnViewLogs);
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

        planeCombo = new JComboBox<>();
        planeCombo.setPreferredSize(new Dimension(180, 30));

        JTextField flightDateField = new JTextField(12);
        JTextField flightTimeField = new JTextField(6);
        flightDateField.setPreferredSize(new Dimension(120, 30));
        flightTimeField.setPreferredSize(new Dimension(80, 30));

        addPlaceholderBehavior(flightDateField, "YYYY-MM-DD");
        addPlaceholderBehavior(flightTimeField, "HH:MM");

        JLabel lblPlane = new JLabel("Uçak Seç:");
        JLabel lblDate = new JLabel("Tarih:");
        JLabel lblTime = new JLabel("Saat:");
        lblPlane.setForeground(textColor);
        lblDate.setForeground(textColor);
        lblTime.setForeground(textColor);

        JButton addFlightBtn = createStyledButton("Sefer Ekle", new Color(70, 70, 70), textColor);

        middlePanel.add(lblPlane);
        middlePanel.add(planeCombo);
        middlePanel.add(lblDate);
        middlePanel.add(flightDateField);
        middlePanel.add(lblTime);
        middlePanel.add(flightTimeField);
        middlePanel.add(addFlightBtn);

        gbc.gridy = 1;
        mainPanel.add(middlePanel, gbc);

        // Alt panel - Uçak butonları listesi (scroll)
        planeListPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        planeListPanel.setBackground(panelColor);

        JScrollPane scrollPane = new JScrollPane(planeListPanel);
        scrollPane.setPreferredSize(new Dimension(1000, 300));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        gbc.gridy = 2;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(scrollPane, gbc);

        // Buton aksiyonları

        addPlaneBtn.addActionListener(e -> {
            String planeName = planeField.getText().trim();
            if (!planeName.isEmpty()) {
                if (ConnectDB.addPlane(planeName)) {
                    logService.log("Yeni uçak eklendi: " + planeName, LogLevel.SUCCESS);
                    planeField.setText("");
                    refreshPlaneList();
                    loadPlanesToCombo();
                } else {
                    logService.log("Uçak eklenirken hata oluştu: " + planeName, LogLevel.ERROR);
                }
            }
        });

        addFlightBtn.addActionListener(e -> {
            String date = flightDateField.getText().trim();
            String time = flightTimeField.getText().trim();
            int selectedIndex = planeCombo.getSelectedIndex();
            
            if (selectedIndex >= 0 && !date.equals("YYYY-MM-DD") && !time.equals("HH:MM")) {
                int planeId = planeIds.get(selectedIndex);
                String planeName = planeCombo.getSelectedItem().toString();
                
                if (ConnectDB.addFlight(planeId, date, time)) {
                    logService.log("Yeni uçuş eklendi: " + planeName + " - " + date + " " + time, LogLevel.SUCCESS);
                    flightDateField.setText("YYYY-MM-DD");
                    flightTimeField.setText("HH:MM");
                } else {
                    logService.log("Uçuş eklenirken hata oluştu: " + planeName + " - " + date + " " + time, LogLevel.ERROR);
                }
            }
        });

        btnViewLogs.addActionListener(e -> showLogs());

        btnSignIn.addActionListener(e -> {
            new SignIn(); 
            dispose();
        });

        btnChoosingScreen.addActionListener(e -> {
            new ChoosingScreen(user); 
            dispose();
        });

        loadPlanesToCombo();
        refreshPlaneList();

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

    private void loadPlanesToCombo() {
        planeCombo.removeAllItems();
        planeIds.clear();

        try (ResultSet rs = ConnectDB.getPlanes()) {
            while (rs != null && rs.next()) {
                int id = rs.getInt("plane_id");
                String name = rs.getString("plane_name");
                planeCombo.addItem(name);
                planeIds.add(id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void refreshPlaneList() {
        planeListPanel.removeAll();

        try (ResultSet rs = ConnectDB.getPlanes()) {
            while (rs != null && rs.next()) {
                int planeId = rs.getInt("plane_id");
                String planeName = rs.getString("plane_name");

                JPanel container = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
                container.setBackground(new Color(40, 40, 40));

                JButton planeBtn = createStyledButton(planeName, new Color(70, 70, 70), Color.WHITE);
                planeBtn.setPreferredSize(new Dimension(200, 40));
                planeBtn.addActionListener(e -> new FlightSeatLayoutFrame(planeId, planeName));

                JButton deleteBtn = createStyledButton("Sil", new Color(150, 50, 50), Color.WHITE);
                deleteBtn.setPreferredSize(new Dimension(80, 40));
                deleteBtn.addActionListener(e -> {
                    int confirm = JOptionPane.showConfirmDialog(this, planeName + " silinsin mi?", "Onay", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        if (ConnectDB.deletePlane(planeId)) {
                            JOptionPane.showMessageDialog(this, "Uçak silindi.");
                            refreshPlaneList();
                            loadPlanesToCombo();
                        } else {
                            JOptionPane.showMessageDialog(this, "Silme işlemi başarısız.");
                        }
                    }
                });

                container.add(planeBtn);
                container.add(deleteBtn);

                planeListPanel.add(container);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        planeListPanel.revalidate();
        planeListPanel.repaint();
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
        SwingUtilities.invokeLater(() -> {
        });
    }
}
