package com.hospital.finance.auth;

import org.springframework.http.HttpStatus;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import javax.servlet.http.HttpSession;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

@RestController
@RequestMapping("/api/finance")
public class PaymentSecurityController {
    private final FinanceAccess access;
    private final PaymentSecurityService security;
    private final JdbcTemplate jdbc;
    public PaymentSecurityController(FinanceAccess access, PaymentSecurityService security, JdbcTemplate jdbc) {
        this.access = access; this.security = security; this.jdbc = jdbc;
    }
    @GetMapping("/session") public Map<String, Object> session(HttpSession session) {
        FinanceAccess.Actor actor = access.require(session);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("csrfToken", FinanceCsrfGuard.token(session)); result.put("role", actor.getRole());
        return result;
    }
    @GetMapping("/payment-password") public Map<String, Boolean> configured(HttpSession session) {
        return Collections.singletonMap("configured", security.configured(patient(access.requireRoles(session, 3))));
    }
    @PutMapping("/payment-password") public Map<String, String> configure(@RequestBody PasswordRequest request, HttpSession session) {
        FinanceAccess.Actor actor = access.requireRoles(session, 3);
        security.configure(patient(actor), actor.getId(), request.loginPassword, request.currentPaymentPassword, request.newPaymentPassword);
        return Collections.singletonMap("message", "系统支付密码已设置（仅限本演示系统）");
    }
    @GetMapping("/cards") public List<Map<String, Object>> cards(HttpSession session) {
        int patientId = patient(access.requireRoles(session, 3));
        return jdbc.query("SELECT id,issuer,last_four,created_at FROM finance_demo_card WHERE patient_id=? ORDER BY created_at,id",
                (rs, row) -> card(rs.getString("id"), rs.getString("issuer"), rs.getString("last_four")), patientId);
    }
    @PostMapping("/cards") @Transactional
    public Map<String, Object> addCard(@RequestBody CardRequest request, HttpSession session) {
        int patientId = patient(access.requireRoles(session, 3));
        if (request.issuer == null || !request.issuer.matches("[\\p{L}\\s（）()·-]{1,80}")
                || request.issuer.trim().isEmpty() || request.lastFour == null || !request.lastFour.matches("[0-9]{4}")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "仅登记演示银行名称和4位尾号，禁止输入真实完整卡号");
        }
        String id = UUID.randomUUID().toString();
        jdbc.update("INSERT INTO finance_demo_card(id,patient_id,issuer,last_four,created_at) VALUES(?,?,?,?,?)",
                id, patientId, request.issuer.trim(), request.lastFour, Timestamp.from(Instant.now()));
        return card(id, request.issuer.trim(), request.lastFour);
    }
    @DeleteMapping("/cards/{id}") @Transactional
    public Map<String, String> removeCard(@PathVariable String id, HttpSession session) {
        int patientId = patient(access.requireRoles(session, 3));
        if (jdbc.update("DELETE FROM finance_demo_card WHERE id=? AND patient_id=?", id, patientId) != 1)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "演示卡不存在");
        return Collections.singletonMap("message", "演示卡已移除");
    }
    private int patient(FinanceAccess.Actor actor) {
        List<Integer> ids = jdbc.query("SELECT id FROM patient WHERE loginid=?", (rs, row) -> rs.getInt("id"), actor.getId());
        if (ids.size() != 1) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "患者账号关联无效");
        return ids.get(0);
    }
    private Map<String, Object> card(String id, String issuer, String lastFour) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", id); result.put("issuer", issuer); result.put("maskedNumber", "**** **** **** " + lastFour);
        result.put("demo", true); result.put("label", "演示卡（非真实银行卡，不连接银行）"); return result;
    }
    // No Lombok/toString: these request secrets must never be logged or persisted in request bodies.
    public static final class PasswordRequest {
        public String loginPassword;
        public String currentPaymentPassword;
        public String newPaymentPassword;
    }
    public static final class CardRequest {
        public String issuer;
        public String lastFour;
        @JsonAnySetter public void rejectUnknown(String field, Object ignored) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "演示卡仅接受银行名称和4位尾号");
        }
    }
}
