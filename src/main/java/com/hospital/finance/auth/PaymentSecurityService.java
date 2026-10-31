package com.hospital.finance.auth;

import com.hospital.dao.LoginMapper;
import com.hospital.entity.Login;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.Map;

/** Independent SYSTEM payment password. Never accepts or stores a real bank password. */
@Service
public class PaymentSecurityService {
    public static final int ITERATIONS = 210000;
    private static final SecureRandom RANDOM = new SecureRandom();
    private final JdbcTemplate jdbc;
    private final LoginMapper logins;
    public PaymentSecurityService(JdbcTemplate jdbc, LoginMapper logins) { this.jdbc = jdbc; this.logins = logins; }

    public boolean configured(int patientId) { return row(patientId, false) != null; }

    public void verify(int patientId, String password) {
        Map<String, Object> stored = row(patientId, false);
        if (stored == null) throw new ResponseStatusException(HttpStatus.CONFLICT, "请先设置本系统支付密码（非银行密码）");
        if (!matches(stored, password)) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "系统支付密码错误");
    }

    @Transactional
    public void configure(int patientId, int loginId, String loginPassword,
                          String currentPaymentPassword, String newPaymentPassword) {
        requirePassword(newPaymentPassword);
        // Defense in depth: the patient must belong to the verified account.
        Integer owner = jdbc.queryForObject("SELECT loginid FROM patient WHERE id=?", Integer.class, patientId);
        if (owner == null || owner != loginId) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权操作该患者");
        Map<String, Object> old = row(patientId, true);
        if (old == null) {
            Login login = logins.selectByPrimaryKey(loginId);
            if (login == null || loginPassword == null || login.getPassword() == null
                    || !LoginPasswordCodec.matches(login.getPassword(),loginPassword)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "登录密码错误");
            }
        } else if (!matches(old, currentPaymentPassword)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "原系统支付密码错误");
        }
        byte[] salt = new byte[24]; RANDOM.nextBytes(salt);
        String encodedSalt = Base64.getEncoder().encodeToString(salt);
        String hash = Base64.getEncoder().encodeToString(derive(newPaymentPassword, salt, ITERATIONS));
        if (old == null) {
            try {
                jdbc.update("INSERT INTO finance_payment_security(patient_id,salt,password_hash,iterations) VALUES(?,?,?,?)",
                        patientId, encodedSalt, hash, ITERATIONS);
            } catch (DuplicateKeyException ex) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "支付密码已设置，请刷新后修改");
            }
        } else {
            jdbc.update("UPDATE finance_payment_security SET salt=?,password_hash=?,iterations=? WHERE patient_id=?",
                    encodedSalt, hash, ITERATIONS, patientId);
        }
    }

    private Map<String, Object> row(int patientId, boolean lock) {
        List<Map<String, Object>> rows = jdbc.queryForList("SELECT salt,password_hash,iterations FROM finance_payment_security WHERE patient_id=?"
                + (lock ? " FOR UPDATE" : ""), patientId);
        return rows.isEmpty() ? null : rows.get(0);
    }
    private boolean matches(Map<String, Object> row, String password) {
        if (password == null || password.length() < 6 || password.length() > 32) return false;
        try {
            int rounds = ((Number) row.get("iterations")).intValue();
            if (rounds < ITERATIONS || rounds > 1000000) return false;
            byte[] salt = Base64.getDecoder().decode((String) row.get("salt"));
            byte[] expected = Base64.getDecoder().decode((String) row.get("password_hash"));
            return MessageDigest.isEqual(expected, derive(password, salt, rounds));
        } catch (IllegalArgumentException ex) { return false; }
    }
    private void requirePassword(String password) {
        if (password == null || password.length() < 6 || password.length() > 32)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "本系统支付密码须为6至32个字符，请勿使用真实银行密码");
    }
    private static byte[] derive(String password, byte[] salt, int rounds) {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, rounds, 256);
        try { return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded(); }
        catch (GeneralSecurityException ex) { throw new IllegalStateException("Password hashing unavailable"); }
        finally { spec.clearPassword(); }
    }
}
