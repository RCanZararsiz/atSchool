import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class ConnectDB {
    private static final String url = "jdbc:sqlite:data/reservation.db";
    static Connection conn;

    static {
        try {
            conn = DriverManager.getConnection(url);
            System.out.println("Veritabanına Bağlanma Başarılı");
            createUsersTable();
            createBusesTable();
            createTripsTable();
            createFlightReservationsTable(); // uçuş rezervasyon tablosu
            createReservationsTable();
            createPlanesTable();    // <-- yeni eklenen
            createFlightsTable();   // <-- yeni eklenen
        } catch (SQLException e) {
            System.out.println("Veritabanına Bağlanma Başarısız");
            e.printStackTrace();
        }
    }
    public static void createPlanesTable() {
        String sql = "CREATE TABLE IF NOT EXISTS planes (" +
                     "plane_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                     "plane_name TEXT UNIQUE NOT NULL)";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void createFlightsTable() {
        String sql = "CREATE TABLE IF NOT EXISTS flights (" +
                     "flight_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                     "plane_id INTEGER," +
                     "flight_date TEXT," +
                     "flight_time TEXT," +
                     "FOREIGN KEY(plane_id) REFERENCES planes(plane_id))";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // Tablo oluşturma metodları
    public static void createUsersTable() {
        String sql = "CREATE TABLE IF NOT EXISTS users (" +
                     "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                     "username TEXT UNIQUE NOT NULL," +
                     "password TEXT NOT NULL," +
                     "role TEXT NOT NULL DEFAULT 'user')";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void createBusesTable() {
        String sql = "CREATE TABLE IF NOT EXISTS buses (" +
                     "bus_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                     "bus_name TEXT UNIQUE NOT NULL)";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void createTripsTable() {
        String sql = "CREATE TABLE IF NOT EXISTS trips (" +
                     "trip_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                     "bus_id INTEGER," +
                     "trip_date TEXT," +
                     "trip_time TEXT," +
                     "FOREIGN KEY(bus_id) REFERENCES buses(bus_id))";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void createFlightReservationsTable() {
        String sql = "CREATE TABLE IF NOT EXISTS flight_reservations (" +
                     "reservation_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                     "flight_id INTEGER," +
                     "seat_number INTEGER," +
                     "user_id INTEGER," +
                     "FOREIGN KEY(flight_id) REFERENCES flights(flight_id)," +
                     "FOREIGN KEY(user_id) REFERENCES users(id))";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void createReservationsTable() {
        String sql = "CREATE TABLE IF NOT EXISTS reservations (" +
                     "reservation_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                     "trip_id INTEGER," +
                     "seat_number INTEGER," +
                     "user_id INTEGER," +                   
                     "FOREIGN KEY(trip_id) REFERENCES trips(trip_id)," +
                     "FOREIGN KEY(user_id) REFERENCES users(id))";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Kullanıcı işlemleri
    public static boolean registerUser(String username, String password) {
        String sql = "INSERT INTO users(username, password) VALUES(?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public static String loginUser(String username, String password) {
        String sql = "SELECT role FROM users WHERE username=? AND password=?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("role");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int getUserId(String username) {
        String sql = "SELECT id FROM users WHERE username=?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next())
                return rs.getInt("id");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // Otobüs işlemleri
    public static boolean addBus(String busName) {
        String sql = "INSERT INTO buses(bus_name) VALUES(?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, busName);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE")) {
                System.out.println("Otobüs ismi zaten mevcut.");
            } else {
                e.printStackTrace();
            }
            return false;
        }
    }

    public static ResultSet getBuses() {
        String sql = "SELECT * FROM buses";
        try {
            Statement st = conn.createStatement();
            return st.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean deleteBus(int busId) {
        String deleteReservationsSQL = """
            DELETE FROM reservations 
            WHERE trip_id IN (SELECT trip_id FROM trips WHERE bus_id = ?)
        """;

        String deleteTripsSQL = "DELETE FROM trips WHERE bus_id = ?";
        String deleteBusSQL = "DELETE FROM buses WHERE bus_id = ?";

        try (
            PreparedStatement deleteResStmt = conn.prepareStatement(deleteReservationsSQL);
            PreparedStatement deleteTripsStmt = conn.prepareStatement(deleteTripsSQL);
            PreparedStatement deleteBusStmt = conn.prepareStatement(deleteBusSQL)
        ) {
            deleteResStmt.setInt(1, busId);
            deleteResStmt.executeUpdate();

            deleteTripsStmt.setInt(1, busId);
            deleteTripsStmt.executeUpdate();

            deleteBusStmt.setInt(1, busId);
            int rowsAffected = deleteBusStmt.executeUpdate();

            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Sefer işlemleri
    public static boolean addTrip(int busId, String date, String time) {
        String sql = "INSERT INTO trips(bus_id, trip_date, trip_time) VALUES(?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, busId);
            pstmt.setString(2, date);
            pstmt.setString(3, time);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static ResultSet getTrips() {
        String sql = "SELECT t.trip_id, b.bus_name, t.trip_date, t.trip_time " +
                     "FROM trips t JOIN buses b ON t.bus_id = b.bus_id";
        try {
            Statement st = conn.createStatement();
            return st.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ResultSet getTripsByBus(int busId) throws SQLException {
        String sql = "SELECT trip_id, trip_date, trip_time FROM trips WHERE bus_id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, busId);
        return ps.executeQuery();
    }

    // Rezervasyon işlemleri
    public static ResultSet getReservedSeats(int tripId) {
        String sql = "SELECT seat_number FROM reservations WHERE trip_id = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, tripId);
            return ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static HashMap<Integer, String> getReservedSeatsWithUsernames(int tripId,HashMap<Integer, String> reservedSeatMap) {
        String sql = "SELECT users.username,reservations.seat_number " +
                "FROM users JOIN reservations ON users.id = reservations.user_id WHERE reservations.trip_id = ?";;
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, tripId);
            ResultSet rs=ps.executeQuery();                
                while (rs != null && rs.next()) {
                    int seatNum = rs.getInt("seat_number");
                    String username = rs.getString("username");
                    reservedSeatMap.put(seatNum, username);
                }
                return reservedSeatMap;
           
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Map<Integer, String> getReservedSeatsWithUser(int tripId) {
        String sql = """
            SELECT r.seat_number, u.username
              FROM reservations r
              JOIN users u ON r.user_id = u.id
             WHERE r.trip_id = ?
        """;
        Map<Integer, String> map = new HashMap<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tripId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    map.put(rs.getInt("seat_number"), rs.getString("username"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }

    public static boolean makeReservation(int tripId, int seatNumber, int userId) {
        String checkSql = "SELECT * FROM reservations WHERE trip_id=? AND seat_number=?";
        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setInt(1, tripId);
            checkStmt.setInt(2, seatNumber);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        String insertSql = "INSERT INTO reservations(trip_id, seat_number, user_id) " +
                           "VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
            pstmt.setInt(1, tripId);
            pstmt.setInt(2, seatNumber);
            pstmt.setInt(3, userId);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean cancelReservation(int tripId, int seatNumber, int userId) {
        String sql = "DELETE FROM reservations WHERE trip_id=? AND seat_number=? AND user_id=?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, tripId);
            pstmt.setInt(2, seatNumber);
            pstmt.setInt(3, userId);
            int deleted = pstmt.executeUpdate();
            return deleted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static ResultSet getReservationsByUser(int userId) {
        String sql = """
            SELECT r.reservation_id, r.trip_id, r.seat_number, t.trip_date, t.trip_time, b.bus_name
              FROM reservations r
              JOIN trips t ON r.trip_id = t.trip_id
              JOIN buses b ON t.bus_id = b.bus_id
             WHERE r.user_id = ?
        """;
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            return ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ResultSet getAllReservations() {
        String sql = """
            SELECT r.reservation_id, u.username, b.bus_name, t.trip_date, t.trip_time, r.seat_number
              FROM reservations r
              JOIN users u ON r.user_id = u.id
              JOIN trips t ON r.trip_id = t.trip_id
              JOIN buses b ON t.bus_id = b.bus_id
        """;
        try {
            Statement stmt = conn.createStatement();
            return stmt.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
 // Sefer silme metodu (ilgili rezervasyonlar da silinir)
    public static boolean deleteTrip(int tripId) {
        String deleteReservationsSQL = "DELETE FROM reservations WHERE trip_id = ?";
        String deleteTripSQL = "DELETE FROM trips WHERE trip_id = ?";

        try {
            conn.setAutoCommit(false); // İşlem bütünlüğü için
            try (PreparedStatement ps1 = conn.prepareStatement(deleteReservationsSQL);
                 PreparedStatement ps2 = conn.prepareStatement(deleteTripSQL)) {

                // Önce rezervasyonları sil
                ps1.setInt(1, tripId);
                ps1.executeUpdate();

                // Sonra seferi sil
                ps2.setInt(1, tripId);
                int affectedRows = ps2.executeUpdate();

                conn.commit(); // Her iki işlem de başarılıysa kalıcı hale getir
                return affectedRows > 0;

            } catch (SQLException e) {
                conn.rollback(); // Hata olursa geri al
                e.printStackTrace();
                return false;
            } finally {
                conn.setAutoCommit(true); // Eski haline döndür
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
   
 // Uçak ekleme
    public static boolean addPlane(String planeName) {
        String sql = "INSERT INTO planes(plane_name) VALUES(?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, planeName);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE")) {
                System.out.println("Uçak ismi zaten mevcut.");
            } else {
                e.printStackTrace();
            }
            return false;
        }
    }

    // Tüm uçakları getir
    public static ResultSet getPlanes() {
        String sql = "SELECT * FROM planes";
        try {
            Statement st = conn.createStatement();
            return st.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Uçak silme (ilgili uçuşlar ve rezervasyonlar da silinir)
    public static boolean deletePlane(int planeId) {
        String deleteReservationsSQL = """
            DELETE FROM reservations 
            WHERE trip_id IN (SELECT flight_id FROM flights WHERE plane_id = ?)
        """;

        String deleteFlightsSQL = "DELETE FROM flights WHERE plane_id = ?";
        String deletePlaneSQL = "DELETE FROM planes WHERE plane_id = ?";

        try (
            PreparedStatement deleteResStmt = conn.prepareStatement(deleteReservationsSQL);
            PreparedStatement deleteFlightsStmt = conn.prepareStatement(deleteFlightsSQL);
            PreparedStatement deletePlaneStmt = conn.prepareStatement(deletePlaneSQL)
        ) {
            deleteResStmt.setInt(1, planeId);
            deleteResStmt.executeUpdate();

            deleteFlightsStmt.setInt(1, planeId);
            deleteFlightsStmt.executeUpdate();

            deletePlaneStmt.setInt(1, planeId);
            int rowsAffected = deletePlaneStmt.executeUpdate();

            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public static boolean makeFlightReservation(int flightId, int seatNumber, int userId) {
        String checkSql = "SELECT * FROM flight_reservations WHERE flight_id=? AND seat_number=?";
        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setInt(1, flightId);
            checkStmt.setInt(2, seatNumber);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) return false; // zaten rezerve edilmiş
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        String insertSql = "INSERT INTO flight_reservations(flight_id, seat_number, user_id) " +
                           "VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
            pstmt.setInt(1, flightId);
            pstmt.setInt(2, seatNumber);
            pstmt.setInt(3, userId);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean cancelFlightReservation(int flightId, int seatNumber, int userId) {
        String sql = "DELETE FROM flight_reservations WHERE flight_id=? AND seat_number=? AND user_id=?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, flightId);
            pstmt.setInt(2, seatNumber);
            pstmt.setInt(3, userId);
            int deleted = pstmt.executeUpdate();
            return deleted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Map<Integer, String> getReservedSeatsForFlight(int flightId) {
        String sql = """
            SELECT fr.seat_number, u.username
              FROM flight_reservations fr
              JOIN users u ON fr.user_id = u.id
             WHERE fr.flight_id = ?
        """;
        Map<Integer, String> map = new HashMap<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, flightId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    map.put(rs.getInt("seat_number"), rs.getString("username"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }

    
 // Uçuş ekleme
    public static boolean addFlight(int planeId, String date, String time) {
        String sql = "INSERT INTO flights(plane_id, flight_date, flight_time) VALUES(?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, planeId);
            pstmt.setString(2, date);
            pstmt.setString(3, time);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Tüm uçuşları getir (uçak adı ile birlikte)
    public static ResultSet getFlights() {
        String sql = "SELECT f.flight_id, p.plane_name, f.flight_date, f.flight_time " +
                     "FROM flights f JOIN planes p ON f.plane_id = p.plane_id";
        try {
            Statement st = conn.createStatement();
            return st.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Bir uçağa ait uçuşları getir
    public static ResultSet getFlightsByPlane(int planeId) throws SQLException {
        String sql = "SELECT flight_id, flight_date, flight_time FROM flights WHERE plane_id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, planeId);
        return ps.executeQuery();
    }

    // Uçuş silme (ilgili rezervasyonlar da silinir)
    public static boolean deleteFlight(int flightId) {
        String deleteReservationsSQL = "DELETE FROM flight_reservations WHERE flight_id = ?";
        String deleteFlightSQL = "DELETE FROM flights WHERE flight_id = ?";

        try {
            conn.setAutoCommit(false); // İşlem bütünlüğü için
            try (PreparedStatement ps1 = conn.prepareStatement(deleteReservationsSQL);
                 PreparedStatement ps2 = conn.prepareStatement(deleteFlightSQL)) {

                // Önce rezervasyonları sil
                ps1.setInt(1, flightId);
                ps1.executeUpdate();

                // Sonra uçuşu sil
                ps2.setInt(1, flightId);
                int affectedRows = ps2.executeUpdate();

                conn.commit(); // Başarılıysa işlemi kaydet
                return affectedRows > 0;

            } catch (SQLException e) {
                conn.rollback(); // Hata varsa işlemi geri al
                e.printStackTrace();
                return false;
            } finally {
                conn.setAutoCommit(true); // AutoCommit'i eski haline getir
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    

    public static HashMap<Integer, String> getReservedSeatsWithUsernamesForFlight(int flightId, HashMap<Integer, String> reservedSeatMap) {
        String sql = "SELECT users.username, flight_reservations.seat_number " +
                     "FROM users JOIN flight_reservations ON users.id = flight_reservations.user_id " +
                     "WHERE flight_reservations.flight_id = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, flightId);
            ResultSet rs = ps.executeQuery();                
            while (rs != null && rs.next()) {
                int seatNum = rs.getInt("seat_number");
                String username = rs.getString("username");
                reservedSeatMap.put(seatNum, username);
            }
            return reservedSeatMap;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getUsername(int userId) {
        String sql = "SELECT username FROM users WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("username");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Bilinmeyen Kullanıcı";
    }

    public static ResultSet getTripInfo(int tripId) {
        String sql = """
            SELECT b.bus_name, t.trip_date, t.trip_time 
            FROM trips t 
            JOIN buses b ON t.bus_id = b.bus_id 
            WHERE t.trip_id = ?
        """;
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, tripId);
            return pstmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ResultSet getFlightInfo(int flightId) {
        String sql = """
            SELECT p.plane_name, f.flight_date, f.flight_time 
            FROM flights f 
            JOIN planes p ON f.plane_id = p.plane_id 
            WHERE f.flight_id = ?
        """;
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, flightId);
            return pstmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    
    
    
    
    
    
    public static void initTestDB() {
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:data/test.db");
            System.out.println("Test veritabanına bağlanıldı.");

            Statement stmt = conn.createStatement();

            // Tabloları temiz kur (varsa silip yeniden oluştur)
            stmt.executeUpdate("DROP TABLE IF EXISTS users");
            stmt.executeUpdate("DROP TABLE IF EXISTS planes");
            stmt.executeUpdate("DROP TABLE IF EXISTS flights");
            stmt.executeUpdate("DROP TABLE IF EXISTS flight_reservations");

            stmt.executeUpdate("""
                CREATE TABLE users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT NOT NULL UNIQUE
                );
            """);

            stmt.executeUpdate("""
                CREATE TABLE planes (
                    plane_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    plane_name TEXT NOT NULL UNIQUE
                );
            """);

            stmt.executeUpdate("""
                CREATE TABLE flights (
                    flight_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    plane_id INTEGER,
                    flight_date TEXT,
                    flight_time TEXT,
                    FOREIGN KEY (plane_id) REFERENCES planes(plane_id)
                );
            """);

            stmt.executeUpdate("""
                CREATE TABLE flight_reservations (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    flight_id INTEGER,
                    seat_number INTEGER,
                    user_id INTEGER,
                    FOREIGN KEY (flight_id) REFERENCES flights(flight_id),
                    FOREIGN KEY (user_id) REFERENCES users(id)
                );
            """);

            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
