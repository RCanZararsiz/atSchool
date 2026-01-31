public class demo {
    public static void main(String[] args) {
        ConnectDB c = new ConnectDB();

        // GUI kodunu EDT üzerinde çalıştır:
        javax.swing.SwingUtilities.invokeLater(() -> {
            new SignIn();
        });
    }
}
