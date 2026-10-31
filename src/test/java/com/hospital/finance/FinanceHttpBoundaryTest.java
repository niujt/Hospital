package com.hospital.finance;

import com.hospital.dao.LoginMapper;
import com.hospital.entity.Login;
import com.hospital.finance.api.*;
import com.hospital.finance.auth.*;
import org.junit.jupiter.api.*;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.Collections;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class FinanceHttpBoundaryTest {
    FinanceService service;LoginMapper logins;MockMvc mvc;
    @BeforeEach void setup(){service=mock(FinanceService.class);logins=mock(LoginMapper.class);FinanceAccess access=new FinanceAccess(logins);mvc=MockMvcBuilders.standaloneSetup(new FinanceController(access,service)).setControllerAdvice(new FinanceErrorHandler()).addInterceptors(new FinanceApiAuthGuard(access),new FinanceCsrfGuard()).build();}
    MockHttpSession session(int id,int role){Login login=new Login();login.setId(id);login.setRole(role);when(logins.selectByPrimaryKey(id)).thenReturn(login);MockHttpSession s=new MockHttpSession();s.setAttribute(FinanceAccess.SESSION_KEY,id);return s;}
    @Test void anonymousApiDoesNotRedirectToHtmlLogin()throws Exception{mvc.perform(get("/api/finance/catalog")).andExpect(status().isUnauthorized());verifyNoInteractions(service);}
    @Test void forgedLegacySessionDoesNotAuthenticateFinance()throws Exception{MockHttpSession s=new MockHttpSession();Login forged=new Login();forged.setRole(1);forged.setId(11);s.setAttribute("login",forged);mvc.perform(get("/api/finance/cash-transactions").session(s)).andExpect(status().isUnauthorized());verifyNoInteractions(service);}
    @Test void patientCannotConfirmRechargeEvenWithCsrf()throws Exception{MockHttpSession s=session(31,3);mvc.perform(post("/api/finance/recharges/id/confirm").session(s).header("X-CSRF-Token",FinanceCsrfGuard.token(s)).header("Idempotency-Key","key")).andExpect(status().isForbidden());verifyNoInteractions(service);}
    @Test void mutationWithoutCsrfFails()throws Exception{MockHttpSession s=session(11,1);mvc.perform(post("/api/finance/recharges/id/confirm").session(s).header("Idempotency-Key","key")).andExpect(status().isForbidden());verifyNoInteractions(service);}
    @Test void patientCannotFetchCommissionPolicy()throws Exception{mvc.perform(get("/api/finance/drug-policies").session(session(31,3))).andExpect(status().isForbidden());verifyNoInteractions(service);}
    @Test void patientCannotFetchCashBook()throws Exception{mvc.perform(get("/api/finance/cash-transactions").session(session(31,3))).andExpect(status().isForbidden());verifyNoInteractions(service);}
    @Test void patientOrderIgnoresSpoofedPatientId()throws Exception{MockHttpSession s=session(31,3);when(service.patientFor(31)).thenReturn(1);when(service.createOrder(31,3,"key",1,1,2,"WALLET",null)).thenReturn(FinanceService.map("id","order","status","PENDING"));mvc.perform(post("/api/finance/orders").session(s).header("X-CSRF-Token",FinanceCsrfGuard.token(s)).header("Idempotency-Key","key").contentType("application/json").content("{\"patientId\":999,\"drugId\":1,\"quantity\":2,\"paymentMethod\":\"WALLET\"}")).andExpect(status().isOk());verify(service).createOrder(31,3,"key",1,1,2,"WALLET",null);}
    @Test void doctorReportIgnoresOtherDoctorQuery()throws Exception{MockHttpSession s=session(21,2);when(service.doctorFor(21)).thenReturn(1);when(service.doctorReport(1,"DAY")).thenReturn(FinanceService.map("points",Collections.emptyList()));mvc.perform(get("/api/finance/reports/doctor?period=DAY&doctorId=999").session(s)).andExpect(status().isOk());verify(service).doctorReport(1,"DAY");}
    @Test void patientLedgerIgnoresOtherPatientQuery()throws Exception{MockHttpSession s=session(31,3);when(service.patientFor(31)).thenReturn(1);when(service.transactions(1,false)).thenReturn(Collections.emptyList());mvc.perform(get("/api/finance/patient-transactions?patientId=999").session(s)).andExpect(status().isOk());verify(service).transactions(1,false);}
    @Test void errorsDoNotExposeDatabaseDetails()throws Exception{MockHttpSession s=session(11,1);when(service.transactions(null,true)).thenThrow(new IllegalStateException("jdbc-secret-db-details"));mvc.perform(get("/api/finance/cash-transactions").session(s)).andExpect(status().isInternalServerError()).andExpect(jsonPath("$.message").value("操作未完成，事务已回滚；请使用原幂等键重试"));}
    @Test void patientCannotCreateDrugOrSetRate()throws Exception{MockHttpSession s=session(31,3);mvc.perform(post("/api/finance/drugs").session(s).header("X-CSRF-Token",FinanceCsrfGuard.token(s)).header("Idempotency-Key","key").contentType("application/json").content("{\"name\":\"fake\",\"type\":0,\"price\":\"1\",\"commissionRate\":\"99\"}")).andExpect(status().isForbidden());verifyNoInteractions(service);}
    @Test void doctorCanReadButCannotEditDrugPolicy()throws Exception{MockHttpSession s=session(21,2);mvc.perform(put("/api/finance/drugs/1").session(s).header("X-CSRF-Token",FinanceCsrfGuard.token(s)).header("Idempotency-Key","key").contentType("application/json").content("{\"name\":\"fake\",\"type\":0,\"price\":\"1\",\"commissionRate\":\"99\"}")).andExpect(status().isForbidden());verifyNoInteractions(service);}

    @Test void adminCanReadAllPatientTransactionsWithoutOptionalFilter()throws Exception{
        MockHttpSession s=session(11,1);when(service.transactions(null,false)).thenReturn(Collections.emptyList());
        mvc.perform(get("/api/finance/patient-transactions").session(s)).andExpect(status().isOk());
        verify(service).transactions(null,false);
    }
    @Test void receiptDoctorDeniedBeforeService()throws Exception{mvc.perform(get("/api/finance/sales/paid-sale/receipt").session(session(21,2))).andExpect(status().isForbidden());verifyNoInteractions(service);}
    @Test void receiptAuthenticatedPatientScopeComesFromSessionAndIsNotCacheable()throws Exception{when(service.receipt(31,3,"paid-sale")).thenReturn(FinanceService.map("notice","模拟票据，非报销凭证","totalAmount","10.00"));mvc.perform(get("/api/finance/sales/paid-sale/receipt?patientId=999").session(session(31,3))).andExpect(status().isOk()).andExpect(header().string("Cache-Control","no-store")).andExpect(jsonPath("$.notice").value("模拟票据，非报销凭证"));verify(service).receipt(31,3,"paid-sale");}
    @Test void anonymousReceiptCannotBeRead()throws Exception{mvc.perform(get("/api/finance/sales/paid-sale/receipt")).andExpect(status().isUnauthorized());verifyNoInteractions(service);}


    @Test void pdfReceiptDoctorDeniedBeforeService()throws Exception{mvc.perform(get("/api/finance/sales/paid-sale/receipt.pdf").session(session(21,2))).andExpect(status().isForbidden());verifyNoInteractions(service);}
    @Test void anonymousPdfReceiptDenied()throws Exception{mvc.perform(get("/api/finance/sales/paid-sale/receipt.pdf")).andExpect(status().isUnauthorized());verifyNoInteractions(service);}
    @Test void foreignPatientOrUnpaidPdfReceiptRemainsNotFound()throws Exception{when(service.receipt(31,3,"missing")).thenThrow(new FinanceException(404,"记录不存在"));mvc.perform(get("/api/finance/sales/missing/receipt.pdf").session(session(31,3))).andExpect(status().isNotFound());}
    @Test void pdfReceiptUsesSessionScopeAndPrivateFixedFilename()throws Exception{
        when(service.receipt(31,3,"paid-sale")).thenReturn(ReceiptPdfTest.fixture());
        byte[] pdf=mvc.perform(get("/api/finance/sales/paid-sale/receipt.pdf?patientId=999").session(session(31,3)))
                .andExpect(status().isOk()).andExpect(content().contentType("application/pdf"))
                .andExpect(header().string("Cache-Control","no-store"))
                .andExpect(header().string("Content-Disposition","inline; filename=\"purchase-receipt.pdf\""))
                .andReturn().getResponse().getContentAsByteArray();
        org.junit.jupiter.api.Assertions.assertEquals("%PDF-",new String(pdf,0,5,java.nio.charset.StandardCharsets.US_ASCII));verify(service).receipt(31,3,"paid-sale");
    }
    @Test void adminCanRetrievePdfReceipt()throws Exception{when(service.receipt(11,1,"paid-sale")).thenReturn(ReceiptPdfTest.fixture());mvc.perform(get("/api/finance/sales/paid-sale/receipt.pdf").session(session(11,1))).andExpect(status().isOk());verify(service).receipt(11,1,"paid-sale");}

    @Test void onlyAdminCanReadOrChangeTaxPolicy()throws Exception{
        for(int role:new int[]{2,3}){MockHttpSession s=session(30+role,role);mvc.perform(get("/api/finance/tax-policy").session(s)).andExpect(status().isForbidden());mvc.perform(put("/api/finance/tax-policy").session(s).header("X-CSRF-Token",FinanceCsrfGuard.token(s)).header("Idempotency-Key","tax").contentType("application/json").content("{\"rate\":\"13\"}")).andExpect(status().isForbidden());}verifyNoInteractions(service);
    }
    @Test void adminTaxSettingUsesCsrfAndIdempotencyKey()throws Exception{
        MockHttpSession s=session(11,1);mvc.perform(put("/api/finance/tax-policy").session(s).header("Idempotency-Key","tax").contentType("application/json").content("{\"rate\":\"13\"}")).andExpect(status().isForbidden());
        when(service.setTaxPolicy(11,"tax","13")).thenReturn(FinanceService.map("rate","13.0000","configured",true));
        mvc.perform(put("/api/finance/tax-policy").session(s).header("X-CSRF-Token",FinanceCsrfGuard.token(s)).header("Idempotency-Key","tax").contentType("application/json").content("{\"rate\":\"13\"}")).andExpect(status().isOk()).andExpect(jsonPath("$.rate").value("13.0000"));verify(service).setTaxPolicy(11,"tax","13");
    }
}
