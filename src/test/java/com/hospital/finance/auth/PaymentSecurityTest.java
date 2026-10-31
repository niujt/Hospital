package com.hospital.finance.auth;

import com.hospital.dao.LoginMapper;
import com.hospital.entity.Login;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class PaymentSecurityTest {
    private final Map<String, Object> stored = new HashMap<>();
    private PaymentSecurityService service(JdbcTemplate jdbc, LoginMapper mapper) {
        when(jdbc.queryForObject(eq("SELECT loginid FROM patient WHERE id=?"), eq(Integer.class), eq(42))).thenReturn(7);
        when(jdbc.queryForList(startsWith("SELECT salt,password_hash"), eq(42))).thenAnswer(invocation ->
                stored.isEmpty() ? Collections.emptyList() : Collections.singletonList(new HashMap<>(stored)));
        when(jdbc.update(startsWith("INSERT INTO finance_payment_security"), any(), any(), any(), any())).thenAnswer(invocation -> {
            stored.put("salt", invocation.getArgument(2)); stored.put("password_hash", invocation.getArgument(3));
            stored.put("iterations", invocation.getArgument(4)); return 1;
        });
        when(jdbc.update(startsWith("UPDATE finance_payment_security"), any(), any(), any(), any())).thenAnswer(invocation -> {
            stored.put("salt", invocation.getArgument(1)); stored.put("password_hash", invocation.getArgument(2));
            stored.put("iterations", invocation.getArgument(3)); return 1;
        });
        Login login = new Login(); login.setId(7); login.setRole(3); login.setPassword("login-test-only");
        when(mapper.selectByPrimaryKey(7)).thenReturn(login);
        return new PaymentSecurityService(jdbc, mapper);
    }
    @Test void initialSetupHashesAndDoesNotPersistRawPassword() {
        JdbcTemplate jdbc = mock(JdbcTemplate.class); LoginMapper mapper = mock(LoginMapper.class);
        PaymentSecurityService service = service(jdbc, mapper);
        assertFalse(service.configured(42));
        service.configure(42, 7, "login-test-only", null, "system-only-123");
        assertTrue(service.configured(42)); assertEquals(210000, stored.get("iterations"));
        assertNotEquals("system-only-123", stored.get("password_hash"));
        assertFalse(stored.toString().contains("login-test-only"));
        assertDoesNotThrow(() -> service.verify(42, "system-only-123"));
        assertThrows(ResponseStatusException.class, () -> service.verify(42, "wrong-one"));
    }
    @Test void initialSetupRequiresLoginPassword() {
        PaymentSecurityService service = service(mock(JdbcTemplate.class), mock(LoginMapper.class));
        assertEquals(403, assertThrows(ResponseStatusException.class,
                () -> service.configure(42, 7, "wrong-login", null, "system-only-123")).getStatus().value());
        assertTrue(stored.isEmpty());
    }
    @Test void changeRequiresOriginalSystemPasswordAndRotatesSalt() {
        PaymentSecurityService service = service(mock(JdbcTemplate.class), mock(LoginMapper.class));
        service.configure(42, 7, "login-test-only", null, "old-system-123");
        String salt = (String) stored.get("salt");
        assertThrows(ResponseStatusException.class,
                () -> service.configure(42, 7, "login-test-only", "wrong-pass", "new-system-123"));
        service.configure(42, 7, null, "old-system-123", "new-system-123");
        assertNotEquals(salt, stored.get("salt"));
        assertThrows(ResponseStatusException.class, () -> service.verify(42, "old-system-123"));
        assertDoesNotThrow(() -> service.verify(42, "new-system-123"));
    }
    @Test void missingPasswordAndInvalidLengthsAreRejected() {
        PaymentSecurityService service = service(mock(JdbcTemplate.class), mock(LoginMapper.class));
        assertEquals(409, assertThrows(ResponseStatusException.class, () -> service.verify(42, "123456")).getStatus().value());
        assertThrows(ResponseStatusException.class, () -> service.configure(42, 7, "login-test-only", null, "12345"));
        assertThrows(ResponseStatusException.class, () -> service.configure(42, 7, "login-test-only", null, String.join("", Collections.nCopies(33, "x"))));
    }
    @Test void cannotConfigureAnotherPatientsPassword() {
        PaymentSecurityService service = service(mock(JdbcTemplate.class), mock(LoginMapper.class));
        assertEquals(403, assertThrows(ResponseStatusException.class,
                () -> service.configure(42, 9, "login-test-only", null, "system-only-123")).getStatus().value());
    }
    @Test void csrfTokenRequiredAndSameOriginCannotBypassIt() {
        FinanceCsrfGuard guard = new FinanceCsrfGuard(); MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/finance/cards");
        request.addHeader("Origin", "http://localhost");
        assertThrows(ResponseStatusException.class, () -> guard.preHandle(request, new MockHttpServletResponse(), new Object()));
        String token = FinanceCsrfGuard.token(request.getSession()); request.addHeader("X-CSRF-Token", token);
        assertTrue(guard.preHandle(request, new MockHttpServletResponse(), new Object()));
    }
    @Test void suppliedCrossOriginRejectedEvenWithToken() {
        FinanceCsrfGuard guard = new FinanceCsrfGuard(); MockHttpServletRequest request = new MockHttpServletRequest("PUT", "/api/finance/payment-password");
        request.addHeader("X-CSRF-Token", FinanceCsrfGuard.token(request.getSession())); request.addHeader("Origin", "https://attacker.invalid");
        assertThrows(ResponseStatusException.class, () -> guard.preHandle(request, new MockHttpServletResponse(), new Object()));
    }
    @Test void csrfRejectsWrongTokenAndAllowsReadWithoutSession() {
        FinanceCsrfGuard guard = new FinanceCsrfGuard(); MockHttpServletRequest request = new MockHttpServletRequest("DELETE", "/api/finance/cards/x");
        FinanceCsrfGuard.token(request.getSession()); request.addHeader("X-CSRF-Token", "not-valid");
        assertThrows(ResponseStatusException.class, () -> guard.preHandle(request, new MockHttpServletResponse(), new Object()));
        assertTrue(guard.preHandle(new MockHttpServletRequest("GET", "/api/finance/session"), new MockHttpServletResponse(), new Object()));
    }
    @Test void csrfTokensAreStablePerSessionAndDistinctAcrossSessions() {
        MockHttpSession first = new MockHttpSession(); MockHttpSession second = new MockHttpSession();
        assertEquals(FinanceCsrfGuard.token(first), FinanceCsrfGuard.token(first));
        assertNotEquals(FinanceCsrfGuard.token(first), FinanceCsrfGuard.token(second));
    }
}
