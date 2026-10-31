package com.hospital.finance.auth;

import com.hospital.dao.LoginMapper;
import com.hospital.entity.Login;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpSession;

/** Only a successfully verified login creates this key. Legacy request/session Login is not trusted. */
@Component
public class FinanceAccess {
    public static final String SESSION_KEY = "financeAuthenticatedLoginId";
    private final LoginMapper loginMapper;

    public FinanceAccess(LoginMapper loginMapper) { this.loginMapper = loginMapper; }

    public Actor require(HttpSession session) {
        Object value = session == null ? null : session.getAttribute(SESSION_KEY);
        if (!(value instanceof Integer)) throw unauthorized();
        Login stored = loginMapper.selectByPrimaryKey((Integer) value);
        if (stored == null || stored.getId() == null || !stored.getId().equals(value)
                || stored.getRole() == null || stored.getRole() < 1 || stored.getRole() > 3) {
            if (session != null) session.removeAttribute(SESSION_KEY);
            throw unauthorized();
        }
        return new Actor(stored.getId(), stored.getRole());
    }

    public Actor requireRoles(HttpSession session, int... roles) {
        Actor actor = require(session);
        for (int role : roles) if (actor.getRole() == role) return actor;
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "该账号无权执行此操作");
    }

    private ResponseStatusException unauthorized() {
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "请重新登录");
    }

    public static final class Actor {
        private final int id;
        private final int role;
        private Actor(int id, int role) { this.id = id; this.role = role; }
        public int getId() { return id; }
        public int getRole() { return role; }
    }
}
