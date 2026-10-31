package com.hospital.finance.api;

import com.hospital.finance.auth.FinanceAccess;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import javax.servlet.http.HttpSession;

@Controller
public class FinancePageController {
    private final FinanceAccess access;
    public FinancePageController(FinanceAccess access){this.access=access;}
    @GetMapping("/finance/patient") public String patient(HttpSession session){access.requireRoles(session,3);return "finance/patient";}
    @GetMapping("/finance/admin") public String admin(HttpSession session){access.requireRoles(session,1);return "finance/admin";}
    @GetMapping("/finance/doctor") public String doctor(HttpSession session){access.requireRoles(session,2);return "finance/doctor";}
}
