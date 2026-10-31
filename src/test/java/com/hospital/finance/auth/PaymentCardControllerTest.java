package com.hospital.finance.auth;

import com.hospital.dao.LoginMapper;
import com.hospital.entity.Login;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.server.ResponseStatusException;
import java.util.Collections;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

class PaymentCardControllerTest {
    private final JdbcTemplate jdbc = mock(JdbcTemplate.class);
    private final LoginMapper mapper = mock(LoginMapper.class);
    private final MockHttpSession session = new MockHttpSession();
    @SuppressWarnings("unchecked")
    private PaymentSecurityController controller(int role) {
        Login login = new Login(); login.setId(7); login.setRole(role);
        when(mapper.selectByPrimaryKey(7)).thenReturn(login);
        session.setAttribute(FinanceAccess.SESSION_KEY, 7);
        when(jdbc.query(eq("SELECT id FROM patient WHERE loginid=?"), any(RowMapper.class), eq(7)))
                .thenReturn(Collections.singletonList(42));
        return new PaymentSecurityController(new FinanceAccess(mapper), mock(PaymentSecurityService.class), jdbc);
    }
    private PaymentSecurityController.CardRequest request(String issuer, String tail) {
        PaymentSecurityController.CardRequest body = new PaymentSecurityController.CardRequest();
        body.issuer = issuer; body.lastFour = tail; return body;
    }
    @Test void demoCardResponseIsMaskedAndAttributedToSessionPatient() {
        Map<String, Object> card = controller(3).addCard(request("测试银行", "1234"), session);
        assertEquals("**** **** **** 1234", card.get("maskedNumber"));
        assertEquals(true, card.get("demo")); assertFalse(card.containsKey("lastFour"));
        assertTrue(card.get("label").toString().contains("非真实银行卡"));
        verify(jdbc).update(startsWith("INSERT INTO finance_demo_card"), any(), eq(42), eq("测试银行"), eq("1234"), any());
    }
    @Test void fullPanCannotBeSentAsTailOrIssuer() {
        PaymentSecurityController controller = controller(3);
        assertThrows(ResponseStatusException.class, () -> controller.addCard(request("测试银行", "1234567890123456"), session));
        assertThrows(ResponseStatusException.class, () -> controller.addCard(request("1234567890123456", "1234"), session));
        verify(jdbc, never()).update(startsWith("INSERT"), any(), any(), any(), any(), any());
    }
    @Test void unexpectedFullCardNumberFieldIsRejectedAtDeserialization() {
        assertThrows(Exception.class, () -> new ObjectMapper().readValue(
                "{\"issuer\":\"测试银行\",\"lastFour\":\"1234\",\"cardNumber\":\"1234567890123456\"}",
                PaymentSecurityController.CardRequest.class));
    }
    @Test void anotherPatientsCardCannotBeDeleted() {
        PaymentSecurityController controller = controller(3);
        when(jdbc.update("DELETE FROM finance_demo_card WHERE id=? AND patient_id=?", "other-card", 42)).thenReturn(0);
        assertEquals(404, assertThrows(ResponseStatusException.class, () -> controller.removeCard("other-card", session)).getStatus().value());
        verify(jdbc).update("DELETE FROM finance_demo_card WHERE id=? AND patient_id=?", "other-card", 42);
    }
    @Test void staffCannotUsePatientCardMutation() {
        PaymentSecurityController controller = controller(1);
        assertEquals(403, assertThrows(ResponseStatusException.class,
                () -> controller.addCard(request("测试银行", "1234"), session)).getStatus().value());
        verifyNoInteractions(jdbc);
    }
}
