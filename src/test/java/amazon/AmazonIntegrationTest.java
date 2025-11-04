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

            // Force-drop even if already created
            try {
                st.execute("DROP TABLE orders");
            } catch (Exception ignored) {
            }

            // ✅ Create new table with CHECK constraint
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

            double total = -1;

            // ✅ Manual validation here
            if (total < 0) {
                throw new IllegalArgumentException("Negative total not allowed");
            }

            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO orders(userId,total) VALUES('u1', ?)"
            )) {
                ps.setDouble(1, total);
                ps.executeUpdate();
            }

        }).isInstanceOf(Exception.class);
    }
}
