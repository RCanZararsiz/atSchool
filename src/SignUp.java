import javax.swing.*;
import java.awt.*;

public class SignUp extends JFrame {

    public SignUp() {
        setTitle("Kayıt Ol");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Tam ekran yap
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(false);

        JPanel bgPanel = new JPanel(new GridBagLayout());
        bgPanel.setBackground(new Color(30, 30, 30));
        add(bgPanel);

        GridBagConstraints gbcMain = new GridBagConstraints();
        gbcMain.insets = new Insets(10, 10, 10, 10);
        gbcMain.gridx = 0;
        gbcMain.gridy = 0;
        gbcMain.fill = GridBagConstraints.HORIZONTAL;

        // Üst başlık
        JLabel headerLabel = new JLabel("Otobüs ve Uçak Rezervasyon Sistemi - Kayıt Ol");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 30));
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerLabel.setPreferredSize(new Dimension(800, 60));
        bgPanel.add(headerLabel, gbcMain);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(50, 50, 50));
        formPanel.setPreferredSize(new Dimension(450, 350));
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Kullanıcı Adı Label
        JLabel userLabel = new JLabel("Kullanıcı Adı:");
        userLabel.setForeground(Color.WHITE);
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        formPanel.add(userLabel, gbc);

        // Kullanıcı Adı TextField
        JTextField userField = new JTextField();
        userField.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.7;
        formPanel.add(userField, gbc);

        // Şifre Label
        JLabel passLabel = new JLabel("Şifre:");
        passLabel.setForeground(Color.WHITE);
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        formPanel.add(passLabel, gbc);

        // Şifre Field
        JPasswordField passField = new JPasswordField();
        passField.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.7;
        formPanel.add(passField, gbc);

        // Kayıt Ol Butonu
        JButton registerBtn = new JButton("Kayıt Ol");
        registerBtn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        registerBtn.setBackground(new Color(0, 120, 215));
        registerBtn.setForeground(Color.WHITE);
        registerBtn.setFocusPainted(false);
        registerBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 1;
        formPanel.add(registerBtn, gbc);

        // Giriş Ekranına Dön Butonu
        JButton backBtn = new JButton("Giriş Ekranına Dön");
        backBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        backBtn.setBackground(new Color(100, 100, 100));
        backBtn.setForeground(Color.WHITE);
        backBtn.setFocusPainted(false);
        backBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.weightx = 1;
        formPanel.add(backBtn, gbc);

        gbcMain.gridy = 1;
        gbcMain.fill = GridBagConstraints.NONE;
        bgPanel.add(formPanel, gbcMain);

        // Buton hover efekti
        registerBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                registerBtn.setBackground(new Color(0, 150, 255));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                registerBtn.setBackground(new Color(0, 120, 215));
            }
        });

        backBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                backBtn.setBackground(new Color(130, 130, 130));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                backBtn.setBackground(new Color(100, 100, 100));
            }
        });

        // Kayıt işlemi
        registerBtn.addActionListener(e -> {
            String user = userField.getText().trim();
            String pass = String.valueOf(passField.getPassword()).trim();
            if (ConnectDB.registerUser(user, pass)) {
                JOptionPane.showMessageDialog(this, "Kayıt Başarılı!");
                dispose();
                new SignIn();
            } else {
                JOptionPane.showMessageDialog(this, "Kayıt başarısız! (Kullanıcı adı alınmış olabilir)");
            }
        });


        backBtn.addActionListener(e -> {
            
            new SignIn();
            dispose();
        });

        setVisible(true);
    }

    
}
