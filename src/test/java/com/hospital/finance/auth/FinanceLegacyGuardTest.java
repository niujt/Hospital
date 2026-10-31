package com.hospital.finance.auth;

import com.hospital.dao.LoginMapper;
import com.hospital.entity.Login;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.server.ResponseStatusException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FinanceLegacyGuardTest {
    private final LoginMapper mapper = mock(LoginMapper.class);
    private final FinanceLegacyGuard guard = new FinanceLegacyGuard(new FinanceAccess(mapper));
    private MockHttpServletRequest request(String method, String path, int role) {
        MockHttpServletRequest request = new MockHttpServletRequest(method, path);
        if (role > 0) {
            Login login = new Login(); login.setId(7); login.setRole(role);
            when(mapper.selectByPrimaryKey(7)).thenReturn(login);
            request.getSession().setAttribute(FinanceAccess.SESSION_KEY, 7);
        }
        return request;
    }
    private boolean check(MockHttpServletRequest request) {
        return guard.preHandle(request, new MockHttpServletResponse(), new Object());
    }
    @Test void legacyInventoryWriteIsBlockedEvenForDoctor() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> check(request("PUT", "/doctor/drug", 2)));
        assertEquals(409, ex.getStatus().value()); assertTrue(ex.getReason().contains("/finance/patient"));
    }
    @Test void trailingSlashAndMatrixParameterCannotBypassStockBlock() {
        assertEquals(409, assertThrows(ResponseStatusException.class,
                () -> check(request("PUT", "/doctor/drug;test=x/", 2))).getStatus().value());
    }
    @Test void patientCannotInvokeClinicalMutationsOrReadOtherClinicalPages() {
        for (String path : new String[]{"/doctor/seekinfo", "/doctor/zation", "/doctor/seek/42", "/doctor/printseek/42", "/doctor/medicalhistory/42", "/doctor/seekMedicalAdvice"}) {
            assertEquals(403, assertThrows(ResponseStatusException.class,
                    () -> check(request("POST", path, 3))).getStatus().value());
        }
    }
    @Test void authenticatedDoctorCanUseClinicalPages() {
        assertTrue(check(request("GET", "/doctor/seek/42", 2)));
        assertTrue(check(request("POST", "/doctor/seekinfo", 2)));
    }
    @Test void departmentLookupRemainsAvailableToPatientForms() {
        assertTrue(check(request("GET", "/doctor/内科", 0))); verifyNoInteractions(mapper);
    }
    @Test void publicSearchReadDoesNotRequireFinanceSession() {
        assertTrue(check(request("GET", "/patient/search", 0)));
        assertTrue(check(request("GET", "/patient/searchinfo", 0))); verifyNoInteractions(mapper);
    }
    @Test void patientSensitivePagesRequireTrustedPatientRole() {
        assertEquals(401, assertThrows(ResponseStatusException.class,
                () -> check(request("GET", "/patient/medicalhistory", 0))).getStatus().value());
        assertEquals(403, assertThrows(ResponseStatusException.class,
                () -> check(request("GET", "/patient/hospitalization", 2))).getStatus().value());
        assertTrue(check(request("POST", "/patient/appointment", 3)));
        assertTrue(check(request("POST", "/patient/downloadpdf", 3)));
    }
}
