import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.ImageIO;

public class ChoosingScreen extends JFrame {

    private BufferedImage busImage;
    private BufferedImage planeImage;

    public ChoosingScreen(String user) {
        setTitle("Otobüs ve Uçak Rezervasyon Sistemi");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 500); 
        setLocationRelativeTo(null); // Ortala
        setResizable(false);

        setLayout(new GridLayout(1, 2, 0, 0)); 

        try {
            busImage = ImageIO.read(getClass().getResource("/bus.jpg"));
            planeImage = ImageIO.read(getClass().getResource("/plane.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Sol panel - Otobüs
        JPanel busPanel = new ImagePanel(busImage);
        busPanel.setLayout(new BorderLayout());

        JButton busButton = createStyledTransparentButton("Otobüs");
        busButton.addActionListener(e -> {
            dispose();
            if (user.equals("Rahmi")) {
                new BusAdminPanel(user);
            } else {
                new BusUserPanel(user);
            }
        });

        JPanel busButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 50));
        busButtonPanel.setOpaque(false);
        busButtonPanel.add(busButton);
        busPanel.add(busButtonPanel, BorderLayout.NORTH);

        // Sağ panel - Uçak
        JPanel planePanel = new ImagePanel(planeImage);
        planePanel.setLayout(new BorderLayout());

        JButton planeButton = createStyledTransparentButton("Uçak");
        planeButton.addActionListener(e -> {
            dispose();
            if (user.equals("Rahmi")) {
                new PlaneAdminPanel(user);
            } else {
                new PlaneUserPanel(user);
            }
        });

        JPanel planeButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 50));
        planeButtonPanel.setOpaque(false);
        planeButtonPanel.add(planeButton);
        planePanel.add(planeButtonPanel, BorderLayout.NORTH);

        add(busPanel);
        add(planePanel);

        setVisible(true);
    }

    private JButton createStyledTransparentButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(180, 50));
        button.setFont(new Font("Segoe UI", Font.BOLD, 22));
        button.setFocusPainted(false);
        button.setContentAreaFilled(false); // Arka plan şeffaf
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setForeground(Color.YELLOW);
                button.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 2));
            }

            public void mouseExited(MouseEvent evt) {
                button.setForeground(Color.WHITE);
                button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
            }
        });

        return button;
    }

    // Resmi panele tam sığdıran panel (boşluk bırakmaz, kırpar)
    class ImagePanel extends JPanel {
        private final Image image;

        public ImagePanel(Image image) {
            this.image = image;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (image != null) {
                int panelWidth = getWidth();
                int panelHeight = getHeight();
                int imgWidth = image.getWidth(this);
                int imgHeight = image.getHeight(this);

                float panelRatio = (float) panelWidth / panelHeight;
                float imageRatio = (float) imgWidth / imgHeight;

                int drawWidth = panelWidth;
                int drawHeight = panelHeight;

                if (panelRatio > imageRatio) {
                    // Panel geniş, resmi yatay büyüt -> dikey kırp
                    drawWidth = panelWidth;
                    drawHeight = (int) (panelWidth / imageRatio);
                } else {
                    // Panel dar, resmi dikey büyüt -> yatay kırp
                    drawHeight = panelHeight;
                    drawWidth = (int) (panelHeight * imageRatio);
                }

                int x = (panelWidth - drawWidth) / 2;
                int y = (panelHeight - drawHeight) / 2;

                g.drawImage(image, x, y, drawWidth, drawHeight, this);
            }
        }
    }
    
}
