import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Statement;

public class ConnectDBTest {

    @BeforeAll
    static void setup() {
        ConnectDB.initTestDB();
    }

    @BeforeEach
    void resetData() throws Exception {
        var stmt = ConnectDB.conn.createStatement();
        stmt.execute("DELETE FROM flight_reservations");
        stmt.execute("DELETE FROM flights");
        stmt.execute("DELETE FROM planes");
        stmt.execute("DELETE FROM users");
        stmt.close();
    }

    @Test
    void testAddPlane() {
        assertTrue(ConnectDB.addPlane("TestPlane1"));
        assertFalse(ConnectDB.addPlane("TestPlane1")); // aynı isim
    }

    @Test
    void testFlightReservationFlow() throws Exception {
        ConnectDB.addPlane("TestPlane");
        var rs = ConnectDB.getPlanes();
        assertTrue(rs.next());
        int planeId = rs.getInt("plane_id");

        assertTrue(ConnectDB.addFlight(planeId, "2025-06-20", "14:00"));
        var flights = ConnectDB.getFlights();
        assertTrue(flights.next());
        int flightId = flights.getInt("flight_id");

        // Kullanıcı ekle
        var stmt = ConnectDB.conn.prepareStatement("INSERT INTO users(username) VALUES(?)", Statement.RETURN_GENERATED_KEYS);
        stmt.setString(1, "user1");
        stmt.executeUpdate();
        var keys = stmt.getGeneratedKeys();
        assertTrue(keys.next());
        int userId = keys.getInt(1);

        // Rezervasyon yap
        assertTrue(ConnectDB.makeFlightReservation(flightId, 3, userId));
        // Aynı koltuk rezerve edilemez
        assertFalse(ConnectDB.makeFlightReservation(flightId, 3, userId));

        // İptal et
        assertTrue(ConnectDB.cancelFlightReservation(flightId, 3, userId));
    }
}
