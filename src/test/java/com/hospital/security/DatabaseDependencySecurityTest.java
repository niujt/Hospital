package com.hospital.security;

import com.mysql.cj.MysqlType;
import com.mysql.cj.jdbc.JdbcPropertySetImpl;
import com.mysql.cj.jdbc.JdbcConnection;
import com.mysql.cj.jdbc.result.ResultSetImpl;
import com.mysql.cj.protocol.a.result.ByteArrayRow;
import com.mysql.cj.protocol.a.result.OkPacket;
import com.mysql.cj.result.DefaultColumnDefinition;
import com.mysql.cj.result.Field;
import org.h2.server.web.WebServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.io.*;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Dependency security behavior guards; no network or production database.
 * The MySQL test exercises removed unsafe auto-deserialization, not a complete
 * reproduction of CVE-2023-22102 (whose advisory does not publish an exploit).
 * The H2 test directly exercises the CVE-2022-45868 plaintext-password boundary.
 */
class DatabaseDependencySecurityTest {
    static class DeserializationProbe implements Serializable {
        private static final long serialVersionUID = 1L;
        static int reads;
        private void readObject(ObjectInputStream input) throws IOException, ClassNotFoundException {
            input.defaultReadObject();
            reads++;
        }
    }

    /** Supplies one server row; the driver's real getObject/getBytes implementation is used. */
    static class OneRowResultSet extends ResultSetImpl {
        OneRowResultSet(byte[] bytes, MysqlType type) { this(bytes, type, true); }
        OneRowResultSet(byte[] bytes, MysqlType type, boolean legacyOption) {
            super(mock(OkPacket.class), null, null);
            JdbcPropertySetImpl properties = new JdbcPropertySetImpl();
            Properties options = new Properties();
            // An old deployment setting must not resurrect removed deserialization behavior.
            options.setProperty("autoDeserialize", Boolean.toString(legacyOption));
            properties.initializeProperties(options);
            this.connection = mock(JdbcConnection.class);
            when(this.connection.getConnectionMutex()).thenReturn(new Object());
            when(this.connection.getPropertySet()).thenReturn(properties);
            Field field = mock(Field.class);
            when(field.getMysqlType()).thenReturn(type);
            when(field.isBinary()).thenReturn(true);
            when(field.isBlob()).thenReturn(true);
            this.columnDefinition = new DefaultColumnDefinition(new Field[] {field});
            this.thisRow = new ByteArrayRow(new byte[][] {bytes}, null);
        }
        @Override protected void checkRowPos() { /* fixed one-row fixture */ }
    }

    @ParameterizedTest
    @EnumSource(value = MysqlType.class, names = {"BLOB", "BIT", "VARBINARY"})
    void binaryResultsNeverDeserializeServerBytesEvenWithLegacyOption(MysqlType type) throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream output = new ObjectOutputStream(bytes)) {
            output.writeObject(new DeserializationProbe());
        }
        DeserializationProbe.reads = 0;
        Object value = new OneRowResultSet(bytes.toByteArray(), type).getObject(1);
        assertEquals(0, DeserializationProbe.reads, type + " must not invoke readObject");
        assertTrue(value instanceof byte[], type + " must remain binary data");
        assertArrayEquals(bytes.toByteArray(), (byte[]) value);
    }

    @Test void ordinaryBinaryAndNullValuesKeepTheirJdbcContract() throws SQLException {
        byte[] payload = new byte[] {0, 1, 2, (byte) 255};
        assertArrayEquals(payload, (byte[]) new OneRowResultSet(payload, MysqlType.BLOB, false).getObject(1));
        assertNull(new OneRowResultSet(null, MysqlType.BLOB, false).getObject(1));
    }

    @Test void existingDriverClassAndJdbcUrlsRemainAvailableWithoutConnecting() throws Exception {
        for (String className : new String[] {"com.mysql.jdbc.Driver", "com.mysql.cj.jdbc.Driver"}) {
            Driver driver = (Driver) Class.forName(className).getDeclaredConstructor().newInstance();
            assertTrue(driver.acceptsURL("jdbc:mysql://localhost:33061/hospital?useSSL=false"));
            assertFalse(driver.acceptsURL("jdbc:h2:mem:unrelated"));
        }
    }

    @Test void h2ConsoleRejectsPlaintextAndMalformedAdminPasswordsBeforeStarting() {
        assertThrows(IllegalArgumentException.class, () -> new WebServer().init(
                "-properties", "null", "-webAdminPassword", "synthetic-test-password"));
        assertThrows(org.h2.message.DbException.class, () -> new WebServer().init(
                "-properties", "null", "-webAdminPassword", "g".repeat(128)));
    }

    @Test void h2ConsoleAcceptsHashedConfigurationWithoutOpeningAListener() {
        // Synthetic 64-byte salt/hash representation, not an actual account credential.
        assertDoesNotThrow(() -> new WebServer().init("-properties", "null",
                "-webAdminPassword", "01".repeat(64)));
        assertDoesNotThrow(() -> new WebServer().init("-properties", "null"));
    }
}
