package com.hospital.finance.api;

import com.hospital.finance.FinanceService;
import com.hospital.finance.FinanceException;
import com.hospital.finance.auth.FinanceAccess;
import com.hospital.finance.auth.FinanceAccess.Actor;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/finance")
public class FinanceController {
    private final FinanceAccess access;
    private final FinanceService service;
    public FinanceController(FinanceAccess access,FinanceService service){this.access=access;this.service=service;}
    // Request DTOs deliberately have no generated toString; secrets never returned.
    public static class DoctorAccountRequest { public Integer doctorId; public String username,password; }
    public static class RechargeRequest { public String amount,channel,paymentPassword,cardId; }
    public static class DrugMetadataRequest { public String name,price,text,commissionRate; public Integer type; }
    public static class PolicyRequest { public String commissionRate; }
    public static class OrderRequest { public Integer patientId,drugId,quantity; public String paymentMethod,encounterId; }
    public static class PayRequest { public String paymentPassword; }
    public static class RefundRequest { public String amount,reason; }
    public static class ProcurementRequest { public Integer drugId,quantity; public String unitCost,reference; }
    public static class EncounterRequest { public Integer appointmentId; }
    public static class TaxPolicyRequest { public String rate; }
    @GetMapping("/tax-policy") public Map<String,Object> taxPolicy(HttpSession session){access.requireRoles(session,1);return service.taxPolicy();}
    @PutMapping("/tax-policy") public Map<String,Object> taxPolicy(HttpSession session,@RequestHeader("Idempotency-Key") String key,@RequestBody TaxPolicyRequest r){Actor a=access.requireRoles(session,1);return service.setTaxPolicy(a.getId(),key,r.rate);}
    @GetMapping("/me") public Map<String,Object> me(HttpSession session){Actor a=access.requireRoles(session,3);return service.wallet(service.patientFor(a.getId()));}
    @GetMapping("/balance") public Map<String,Object> balance(HttpSession session,@RequestParam int patientId){access.requireRoles(session,1);return service.wallet(patientId);}
    @PostMapping("/recharges") public Map<String,Object> recharge(HttpSession session,@RequestHeader("Idempotency-Key") String key,@RequestBody RechargeRequest r){Actor a=access.requireRoles(session,3);return service.recharge(a.getId(),service.patientFor(a.getId()),key,r.amount,r.channel,r.paymentPassword,r.cardId);}
    @GetMapping("/recharges") public List<Map<String,Object>> recharges(HttpSession session){Actor a=access.requireRoles(session,1,3);return service.recharges(a.getRole()==3?service.patientFor(a.getId()):null);}
    @PostMapping("/recharges/{id}/confirm") public Map<String,Object> confirm(HttpSession session,@RequestHeader("Idempotency-Key") String key,@PathVariable String id){Actor a=access.requireRoles(session,1);return service.confirmRecharge(a.getId(),key,id);}
    @GetMapping("/catalog") public List<Map<String,Object>> catalog(HttpSession session){access.require(session);return service.catalog(false);}
    @PostMapping("/drugs") public Map<String,Object> addDrug(HttpSession session,@RequestHeader("Idempotency-Key") String key,@RequestBody DrugMetadataRequest r){Actor a=access.requireRoles(session,1);return service.saveDrug(a.getId(),key,null,r.name,r.type,r.price,r.text,r.commissionRate);}
    @PutMapping("/drugs/{id}") public Map<String,Object> editDrug(HttpSession session,@RequestHeader("Idempotency-Key") String key,@PathVariable int id,@RequestBody DrugMetadataRequest r){Actor a=access.requireRoles(session,1);return service.saveDrug(a.getId(),key,id,r.name,r.type,r.price,r.text,r.commissionRate);}
    @GetMapping("/drug-policies") public List<Map<String,Object>> policies(HttpSession session){access.requireRoles(session,1,2);return service.catalog(true);}
    @PutMapping("/drug-policies/{id}") public Map<String,Object> policy(HttpSession session,@RequestHeader("Idempotency-Key") String key,@PathVariable int id,@RequestBody PolicyRequest r){Actor a=access.requireRoles(session,1);return service.setPolicy(a.getId(),key,id,r.commissionRate);}
    @GetMapping("/orders") public List<Map<String,Object>> orders(HttpSession session){Actor a=access.require(session);return service.orders(a.getId(),a.getRole());}
    @PostMapping("/orders") public Map<String,Object> order(HttpSession session,@RequestHeader("Idempotency-Key") String key,@RequestBody OrderRequest r){Actor a=access.require(session);int patient=a.getRole()==3?service.patientFor(a.getId()):required(r.patientId);return service.createOrder(a.getId(),a.getRole(),key,patient,required(r.drugId),required(r.quantity),r.paymentMethod,r.encounterId);}
    @PostMapping("/orders/{id}/pay") public Map<String,Object> pay(HttpSession session,@RequestHeader("Idempotency-Key") String key,@PathVariable String id,@RequestBody PayRequest r){Actor a=access.require(session);return service.payOrder(a.getId(),a.getRole(),key,id,r.paymentPassword);}
    @GetMapping("/sales") public List<Map<String,Object>> sales(HttpSession session){Actor a=access.require(session);return service.sales(a.getRole()==3?service.patientFor(a.getId()):null,a.getRole()==2?service.doctorFor(a.getId()):null,a.getRole()!=3);}
    @GetMapping("/sales/{id}/receipt.pdf")
    public org.springframework.http.ResponseEntity<byte[]> receiptPdf(HttpSession session,@PathVariable String id) {
        Actor a=access.requireRoles(session,1,3);
        Map<String,Object> receipt;
        try { receipt=service.receipt(a.getId(),a.getRole(),id); }
        catch (FinanceException ex) {
            // A .pdf suffix negotiates PDF, so do not delegate a Map error body to a JSON converter.
            return org.springframework.http.ResponseEntity.status(ex.getStatus()).header("Cache-Control","no-store").build();
        }
        byte[] pdf=com.hospital.finance.ReceiptPdf.render(receipt);
        return org.springframework.http.ResponseEntity.ok()
                .header("Cache-Control","no-store")
                .header("Content-Disposition","inline; filename=\"purchase-receipt.pdf\"")
                .contentType(org.springframework.http.MediaType.APPLICATION_PDF).body(pdf);
    }
    @GetMapping("/sales/{id}/receipt") public Map<String,Object> receipt(HttpSession session,@PathVariable String id,javax.servlet.http.HttpServletResponse response){Actor a=access.requireRoles(session,1,3);response.setHeader("Cache-Control","no-store");return service.receipt(a.getId(),a.getRole(),id);}
    @PostMapping("/sales/{id}/refund") public Map<String,Object> refund(HttpSession session,@RequestHeader("Idempotency-Key") String key,@PathVariable String id,@RequestBody RefundRequest r){Actor a=access.requireRoles(session,1);return service.refund(a.getId(),key,id,r.amount,r.reason);}
    @GetMapping("/procurements") public List<Map<String,Object>> procurements(HttpSession session){access.requireRoles(session,1);return service.procurements();}
    @PostMapping("/procurements") public Map<String,Object> procure(HttpSession session,@RequestHeader("Idempotency-Key") String key,@RequestBody ProcurementRequest r){Actor a=access.requireRoles(session,1);return service.procure(a.getId(),key,required(r.drugId),required(r.quantity),r.unitCost,r.reference);}
    @GetMapping("/encounters") public List<Map<String,Object>> encounters(HttpSession session){Actor a=access.require(session);return service.encounters(a.getId(),a.getRole());}
    @PostMapping("/encounters/complete") public Map<String,Object> complete(HttpSession session,@RequestHeader("Idempotency-Key") String key,@RequestBody EncounterRequest r){Actor a=access.requireRoles(session,2);return service.completeEncounter(a.getId(),key,required(r.appointmentId));}
    @GetMapping("/patient-transactions") public List<Map<String,Object>> patientTransactions(HttpSession session,@RequestParam(required=false) Integer patientId){Actor a=access.requireRoles(session,1,3);Integer effectivePatient = a.getRole()==3 ? Integer.valueOf(service.patientFor(a.getId())) : patientId; return service.transactions(effectivePatient,false);}
    @GetMapping("/cash-transactions") public List<Map<String,Object>> cash(HttpSession session){access.requireRoles(session,1);return service.transactions(null,true);}
    @GetMapping("/reports/cash") public Map<String,Object> cashReport(HttpSession session,@RequestParam(defaultValue="MONTH") String period){access.requireRoles(session,1);return service.cashReport(period);}
    @GetMapping("/reports/doctor") public Map<String,Object> doctorReport(HttpSession session,@RequestParam(defaultValue="MONTH") String period,@RequestParam(required=false) Integer doctorId){Actor a=access.requireRoles(session,1,2);return service.doctorReport(a.getRole()==2?service.doctorFor(a.getId()):required(doctorId),period);}
    @GetMapping("/encounters/appointments") public List<Map<String,Object>> appointments(HttpSession session,@RequestParam(required=false) Integer doctorId){Actor a=access.requireRoles(session,1,2);return service.appointments(a.getId(),a.getRole(),doctorId);}
    @GetMapping("/patients") public List<Map<String,Object>> patients(HttpSession session){access.requireRoles(session,1);return service.patients();}
    @PostMapping("/doctor-accounts") public Map<String,Object> assignDoctor(HttpSession session,@RequestHeader("Idempotency-Key") String key,@RequestBody DoctorAccountRequest r){Actor a=access.requireRoles(session,1);return service.assignDoctorAccount(a.getId(),key,required(r.doctorId),r.username,r.password);}
    @GetMapping("/doctors") public List<Map<String,Object>> doctors(HttpSession session){access.requireRoles(session,1);return service.doctors();}
    private static int required(Integer value){if(value==null||value<=0)throw new FinanceException(400,"缺少有效的ID或数量");return value;}
}
