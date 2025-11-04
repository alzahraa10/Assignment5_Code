package amazon;

import org.junit.jupiter.api.*;
import java.sql.*;
import static org.assertj.core.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AmazonIntegrationTest {

    Connection conn;

    @BeforeAll
    void init() throws Exception {
        conn = DriverManager.getConnection(
                "jdbc:hsqldb:mem:amazon", "sa", ""
        );
        try (Statement st = conn.createStatement()) {

            // Drop table if it already exists
            st.execute("DROP TABLE IF EXISTS orders");

            // Create table with CHECK constraint to prevent negative totals
            st.execute(
                    "CREATE TABLE orders (" +
                            "id INT IDENTITY, " +
                            "userId VARCHAR(50), " +
                            "total DOUBLE, " +
                            "CONSTRAINT non_negative CHECK (total >= 0))"
            );
        }
    }

    @BeforeEach
    void reset() throws Exception {
        try (Statement st = conn.createStatement()) {
            st.execute("DELETE FROM orders");
        }
    }

    @AfterAll
    void close() throws Exception {
        conn.close();
    }

    @Test
    @DisplayName("specification-based")
    void saveOrder() throws Exception {
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO orders(userId,total) VALUES('u1',20)"
        )) {
            ps.executeUpdate();
        }

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM orders")) {
            rs.next();
            int count = rs.getInt(1);
            assertThat(count).isEqualTo(1);
        }
    }

    @Test
    @DisplayName("structural-based")
    void noNegative() {
        assertThatThrownBy(() -> {
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO orders(userId,total) VALUES('u1',-1)"
            )) {
                ps.executeUpdate();
            }
        }).isInstanceOf(Exception.class);
    }
}
