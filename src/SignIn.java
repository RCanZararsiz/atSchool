import javax.swing.*;
import java.awt.*;

public class SignIn extends JFrame {

    public SignIn() {
        setTitle("Giriş Yap");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

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
        JLabel headerLabel = new JLabel("Otobüs ve Uçak Rezervasyon Sistemi");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 30));
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerLabel.setPreferredSize(new Dimension(700, 60));
        bgPanel.add(headerLabel, gbcMain);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(50, 50, 50));
        formPanel.setPreferredSize(new Dimension(450, 300));
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
        gbc.weightx = 0.7;       // genişlik artırıldı
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
        gbc.weightx = 0.7;       // genişlik artırıldı
        formPanel.add(passField, gbc);

        // Giriş Butonu
        JButton loginBtn = new JButton("Giriş Yap");
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        loginBtn.setBackground(new Color(0, 120, 215));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);
        loginBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 1;
        formPanel.add(loginBtn, gbc);

        // Kayıt Ol Butonu
        JButton signUpBtn = new JButton("Kayıt Ol");
        signUpBtn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        signUpBtn.setBackground(new Color(60, 60, 60));
        signUpBtn.setForeground(Color.WHITE);
        signUpBtn.setFocusPainted(false);
        signUpBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        gbc.gridy = 3;
        formPanel.add(signUpBtn, gbc);

        gbcMain.gridy = 1;
        gbcMain.fill = GridBagConstraints.NONE;
        bgPanel.add(formPanel, gbcMain);

        // Buton hover efekti
        loginBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                loginBtn.setBackground(new Color(0, 150, 255));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                loginBtn.setBackground(new Color(0, 120, 215));
            }
        });
        signUpBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                signUpBtn.setBackground(new Color(80, 80, 80));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                signUpBtn.setBackground(new Color(60, 60, 60));
            }
        });

        // Giriş işlemi
        loginBtn.addActionListener(e -> {
            String user = userField.getText().trim();
            String pass = String.valueOf(passField.getPassword()).trim();

            String role = ConnectDB.loginUser(user, pass);
            if (role != null) {
                JOptionPane.showMessageDialog(this, "Giriş Başarılı! Hoşgeldin " + user);
                new ChoosingScreen(user);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Kullanıcı adı veya şifre hatalı.");
            }
        });

        signUpBtn.addActionListener(e -> {
            
            new SignUp();
            dispose();
        });

        setVisible(true);
    }

}
