package com.hospital.finance;

import com.hospital.controller.PatientController;
import com.hospital.dao.LoginMapper;
import com.hospital.entity.*;
import com.hospital.finance.auth.FinanceAccess;
import com.hospital.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PatientAppointmentOwnershipTest {
    @Test void requestPatientIdCannotBookForAnotherPatient(){
        LoginMapper mapper=mock(LoginMapper.class);Login login=new Login();login.setId(31);login.setRole(3);when(mapper.selectByPrimaryKey(31)).thenReturn(login);
        PatientService patients=mock(PatientService.class);Patient owner=new Patient();owner.setId(1);when(patients.findPatientByLoginId(31)).thenReturn(owner);
        AppointmentService appointments=mock(AppointmentService.class);when(appointments.addAppointment(any())).thenReturn("添加成功");when(appointments.selectTheLastAppointment(1)).thenReturn(77);
        PatientController controller=new PatientController();ReflectionTestUtils.setField(controller,"financeAccess",new FinanceAccess(mapper));ReflectionTestUtils.setField(controller,"patientService",patients);ReflectionTestUtils.setField(controller,"appointmentService",appointments);
        MockHttpSession session=new MockHttpSession();session.setAttribute(FinanceAccess.SESSION_KEY,31);Appointment request=new Appointment();request.setPatientid(999);request.setDoctorid(1);
        controller.appointment(request,session);
        assertEquals(Integer.valueOf(1),request.getPatientid());verify(appointments).addAppointment(argThat(a->a.getPatientid()==1));verify(appointments,never()).selectTheLastAppointment(999);verify(patients).updateAppointMent(argThat(p->p.getId()==1));
    }
    @Test void legacyForgedSessionCannotBook(){PatientController controller=new PatientController();ReflectionTestUtils.setField(controller,"financeAccess",new FinanceAccess(mock(LoginMapper.class)));MockHttpSession session=new MockHttpSession();Login forged=new Login();forged.setId(31);forged.setRole(3);session.setAttribute("login",forged);assertThrows(ResponseStatusException.class,()->controller.appointment(new Appointment(),session));}
}
