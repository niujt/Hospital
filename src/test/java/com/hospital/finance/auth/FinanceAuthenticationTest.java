package com.hospital.finance.auth;

import com.hospital.controller.LoginController;
import com.hospital.dao.LoginMapper;
import com.hospital.dao.PatientMapper;
import com.hospital.dao.DoctorMapper;
import com.hospital.entity.Login;
import com.hospital.entity.Patient;
import com.hospital.service.LoginService;
import com.hospital.service.impl.LoginServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FinanceAuthenticationTest {
    private Login account(int id, int role) {
        Login login = new Login(); login.setId(id); login.setRole(role);
        login.setUsername("synthetic"); login.setPassword("test-only-password"); return login;
    }
    @Test void legacySessionObjectDoesNotAuthenticateFinance() {
        LoginMapper mapper = mock(LoginMapper.class);
        MockHttpSession session = new MockHttpSession(); session.setAttribute("login", account(1, 1));
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> new FinanceAccess(mapper).require(session));
        assertEquals(401, ex.getStatus().value()); verifyNoInteractions(mapper);
    }
    @Test void actorUsesDatabaseRoleNotLegacySessionRole() {
        LoginMapper mapper = mock(LoginMapper.class); when(mapper.selectByPrimaryKey(7)).thenReturn(account(7, 3));
        MockHttpSession session = new MockHttpSession(); session.setAttribute(FinanceAccess.SESSION_KEY, 7);
        session.setAttribute("login", account(7, 1));
        FinanceAccess access = new FinanceAccess(mapper);
        assertEquals(3, access.require(session).getRole());
        assertEquals(403, assertThrows(ResponseStatusException.class, () -> access.requireRoles(session, 1)).getStatus().value());
    }
    @Test void removedAccountInvalidatesTrustedKey() {
        LoginMapper mapper = mock(LoginMapper.class);
        MockHttpSession session = new MockHttpSession(); session.setAttribute(FinanceAccess.SESSION_KEY, 7);
        assertThrows(ResponseStatusException.class, () -> new FinanceAccess(mapper).require(session));
        assertNull(session.getAttribute(FinanceAccess.SESSION_KEY));
    }
    @Test void badLoginClearsOldIdentityAndIgnoresBoundIdRole() {
        LoginService service = mock(LoginService.class);
        LoginController controller = new LoginController(); ReflectionTestUtils.setField(controller, "loginService", service);
        MockHttpSession session = new MockHttpSession(); session.setAttribute("login", account(1, 1));
        session.setAttribute(FinanceAccess.SESSION_KEY, 1);
        when(service.login(any(Login.class))).thenAnswer(call -> {
            Login credentials = call.getArgument(0); assertNull(credentials.getId()); assertNull(credentials.getRole());
            return "密码错误";
        });
        assertEquals("密码错误", controller.login(account(1, 1), session).get("message"));
        assertNull(session.getAttribute("login")); assertNull(session.getAttribute(FinanceAccess.SESSION_KEY));
    }
    @Test void verifiedLoginStoresOnlyVerifiedIdentityAndNoPassword() {
        LoginService service = mock(LoginService.class);
        LoginController controller = new LoginController(); ReflectionTestUtils.setField(controller, "loginService", service);
        when(service.login(any(Login.class))).thenAnswer(call -> {
            Login credentials = call.getArgument(0); credentials.setId(8); credentials.setRole(3); return "登录成功3";
        });
        MockHttpSession session = new MockHttpSession();
        assertEquals("登录成功3", controller.login(account(1, 1), session).get("message"));
        Login stored = (Login) session.getAttribute("login");
        assertEquals(Integer.valueOf(8), stored.getId()); assertEquals(Integer.valueOf(3), stored.getRole());
        assertNull(stored.getPassword()); assertEquals(8, session.getAttribute(FinanceAccess.SESSION_KEY));
    }
    @Test void logoutClearsBothIdentityRepresentations() {
        MockHttpSession session = new MockHttpSession(); session.setAttribute("login", account(1, 1));
        session.setAttribute(FinanceAccess.SESSION_KEY, 1);
        assertEquals("redirect:/hospital", new LoginController().loginout(session));
        assertNull(session.getAttribute("login")); assertNull(session.getAttribute(FinanceAccess.SESSION_KEY));
    }
    @Test void serviceDropsForgedRoleOnWrongCredentials() {
        LoginMapper mapper = mock(LoginMapper.class); when(mapper.findByUsername("synthetic")).thenReturn(account(8, 3));
        LoginServiceImpl service = new LoginServiceImpl(); ReflectionTestUtils.setField(service, "loginMapper", mapper);
        Login input = account(1, 1); input.setPassword("wrong");
        assertEquals("密码错误", service.login(input)); assertNull(input.getId()); assertNull(input.getRole());
    }
    @Test void serviceUsesStoredIdentityOnCorrectCredentials() {
        LoginMapper mapper = mock(LoginMapper.class); when(mapper.findByUsername("synthetic")).thenReturn(account(8, 3));
        LoginServiceImpl service = new LoginServiceImpl(); ReflectionTestUtils.setField(service, "loginMapper", mapper);
        when(mapper.migratePassword(eq(8), anyString(), anyString())).thenReturn(1);
        Login input = account(1, 1); assertEquals("登录成功3", service.login(input));
        assertEquals(Integer.valueOf(8), input.getId()); assertEquals(Integer.valueOf(3), input.getRole());
    }
    @Test void publicRegistrationCannotCreateAdministrator() {
        LoginMapper mapper = mock(LoginMapper.class); LoginServiceImpl service = new LoginServiceImpl();
        ReflectionTestUtils.setField(service, "loginMapper", mapper);
        Login input = account(1, 1); input.setCertId("  ");
        assertEquals("管理员账号须由已登录管理员创建", service.regist(input));
        assertNull(input.getRole()); assertNull(input.getId()); verify(mapper, never()).insert(any());
    }
    @Test void registrationIdentityMustExist() {
        LoginMapper mapper = mock(LoginMapper.class); LoginServiceImpl service = new LoginServiceImpl();
        ReflectionTestUtils.setField(service, "loginMapper", mapper);
        ReflectionTestUtils.setField(service, "doctorMapper", mock(DoctorMapper.class));
        ReflectionTestUtils.setField(service, "patientMapper", mock(PatientMapper.class));
        Login input = account(1, 1); input.setCertId("synthetic-cert");
        assertEquals("该证件信息未入库，不能注册该医生或者患者", service.regist(input)); verify(mapper, never()).insert(any());
    }
    @Test void patientRegistrationDerivesRoleAndChecksBindingWrite() {
        LoginMapper mapper = mock(LoginMapper.class); PatientMapper patients = mock(PatientMapper.class);
        LoginServiceImpl service = new LoginServiceImpl(); ReflectionTestUtils.setField(service, "loginMapper", mapper);
        ReflectionTestUtils.setField(service, "doctorMapper", mock(DoctorMapper.class));
        ReflectionTestUtils.setField(service, "patientMapper", patients);
        Patient patient = new Patient(); patient.setId(42);
        when(patients.findPatientByCertId("synthetic-cert")).thenReturn(patient);
        when(mapper.findByUsername("synthetic")).thenReturn(null, account(9, 3));
        when(mapper.insert(any())).thenReturn(1); when(patients.updateByPrimaryKeySelective(patient)).thenReturn(1);
        Login input = account(1, 1); input.setCertId("synthetic-cert");
        assertEquals("注册成功", service.regist(input)); assertEquals(Integer.valueOf(3), input.getRole());
        assertEquals(Integer.valueOf(9), patient.getLoginid());
    }
    @Test void adminGuardRejectsPatientBeforeController() {
        LoginMapper mapper = mock(LoginMapper.class); when(mapper.selectByPrimaryKey(8)).thenReturn(account(8, 3));
        MockHttpServletRequest request = new MockHttpServletRequest(); request.getSession().setAttribute(FinanceAccess.SESSION_KEY, 8);
        FinanceAdminGuard guard = new FinanceAdminGuard(new FinanceAccess(mapper));
        assertEquals(403, assertThrows(ResponseStatusException.class,
                () -> guard.preHandle(request, new MockHttpServletResponse(), new Object())).getStatus().value());
    }
    @Test void adminGuardAllowsVerifiedAdministrator() {
        LoginMapper mapper = mock(LoginMapper.class); when(mapper.selectByPrimaryKey(8)).thenReturn(account(8, 1));
        MockHttpServletRequest request = new MockHttpServletRequest(); request.getSession().setAttribute(FinanceAccess.SESSION_KEY, 8);
        assertTrue(new FinanceAdminGuard(new FinanceAccess(mapper)).preHandle(request, new MockHttpServletResponse(), new Object()));
    }
    @Test void unsupportedStoredRoleFailsClosed() {
        LoginMapper mapper = mock(LoginMapper.class); when(mapper.selectByPrimaryKey(7)).thenReturn(account(7, 99));
        MockHttpSession session = new MockHttpSession(); session.setAttribute(FinanceAccess.SESSION_KEY, 7);
        assertEquals(401, assertThrows(ResponseStatusException.class, () -> new FinanceAccess(mapper).require(session)).getStatus().value());
        assertNull(session.getAttribute(FinanceAccess.SESSION_KEY));
    }
    @Test void successfulHttpLoginRotatesSessionId() {
        LoginService service = mock(LoginService.class);
        LoginController controller = new LoginController(); ReflectionTestUtils.setField(controller, "loginService", service);
        when(service.login(any(Login.class))).thenAnswer(call -> {
            Login credentials = call.getArgument(0); credentials.setId(8); credentials.setRole(3); return "登录成功3";
        });
        MockHttpServletRequest request = new MockHttpServletRequest();
        javax.servlet.http.HttpSession session = request.getSession();
        String oldId = session.getId();
        org.springframework.web.context.request.RequestContextHolder.setRequestAttributes(
                new org.springframework.web.context.request.ServletRequestAttributes(request));
        try { controller.login(account(1, 1), session); assertNotEquals(oldId, session.getId()); }
        finally { org.springframework.web.context.request.RequestContextHolder.resetRequestAttributes(); }
    }

}
