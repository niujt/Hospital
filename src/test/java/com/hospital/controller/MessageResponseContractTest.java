package com.hospital.controller;

import com.alibaba.fastjson2.JSONObject;
import com.hospital.entity.*;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Characterizes the existing message-only endpoints without starting Spring or a database.
 * The explicit table deliberately stays independent of production response helpers.
 */
class MessageResponseContractTest {
    private static final Object[][] ENDPOINTS = {
            {"LoginController", "adminAdd", Login.class, "loginService", "addAmin"},
            {"LoginController", "upAdmin", Login.class, "loginService", "updateAdmin"},
            {"LoginController", "delAdmin", Integer.class, "loginService", "delAdmin"},
            {"LoginController", "regest", Login.class, "loginService", "regist"},
            {"MedicalhistoryController", "delmedicalhistory", Integer.class, "medicalhistoryService", "delMedicalhistory"},
            {"MedicalhistoryController", "medicalhistoryUpdate", Medicalhistory.class, "medicalhistoryService", "UpdateMedicalhistory"},
            {"MedicalhistoryController", "medicalhistoryAdd", Medicalhistory.class, "medicalhistoryService", "addMedicalhistory"},
            {"OptionController", "delOption", Integer.class, "optionService", "deleteOption"},
            {"OptionController", "addOption", Option.class, "optionService", "insert"},
            {"OptionController", "updateOption", Option.class, "optionService", "updateBOption"},
            {"DrugsController", "delDrug", Integer.class, "drugsService", "delDrug"},
            {"DrugsController", "addDrug", Drugs.class, "drugsService", "addDrug"},
            {"DrugsController", "updateDrug", Drugs.class, "drugsService", "updateDrug"},
            {"DoctorController", "delDoctor", Integer.class, "doctorService", "delDoctor"},
            {"DoctorController", "AddDoctor", Doctor.class, "doctorService", "addDoctor"},
            {"DoctorController", "updateDoctor", Doctor.class, "doctorService", "upDoctor"},
            {"DoctorController", "zation", Hospitalization.class, "hospitalizationService", "AddHospitalization"},
            {"HospitalizationController", "hospitalizationAdd", Hospitalization.class, "hospitalizationService", "AddHospitalization"},
            {"HospitalizationController", "delHospitalization", Integer.class, "hospitalizationService", "deleteHospitalization"},
            {"HospitalizationController", "delHospitalization", Hospitalization.class, "hospitalizationService", "updateHospitalization"},
            {"AppointmentController", "delAppointment", Integer.class, "appointmentService", "delAppointment"},
            {"AppointmentController", "AppointmentUpdate", Appointment.class, "appointmentService", "UpdateAppointment"},
            {"AppointmentController", "AppointmentAdd", Appointment.class, "appointmentService", "addAppointment"},
            {"PatientController", "delPatient", Integer.class, "patientService", "delPatient"},
            {"PatientController", "patientInfo", Patient.class, "patientService", "updatePatient"},
            {"PatientController", "delPatient", Patient.class, "patientService", "addPatient"},
            {"IllnessController", "delIllness", Integer.class, "illnessService", "deleteIllness"},
            {"IllnessController", "addDrug", Illness.class, "illnessService", "insert"},
            {"IllnessController", "updateIllness", Illness.class, "illnessService", "updateIllness"}
    };

    @TestFactory
    List<DynamicTest> messageEndpointsPreserveServiceResultAndDelegation() {
        List<DynamicTest> tests = new ArrayList<>();
        for (Object[] endpoint : ENDPOINTS) {
            for (String message : new String[]{"操作成功", "操作失败", "", null}) {
                tests.add(DynamicTest.dynamicTest(endpoint[0] + "." + endpoint[1]
                        + "(" + ((Class<?>) endpoint[2]).getSimpleName() + "): " + message,
                        () -> assertContract(endpoint, message)));
            }
        }
        return tests;
    }

    private void assertContract(Object[] endpoint, String message) throws Exception {
        Class<?> controllerType = Class.forName("com.hospital.controller." + endpoint[0]);
        Object controller = controllerType.getDeclaredConstructor().newInstance();
        Class<?> argumentType = (Class<?>) endpoint[2];
        Object argument = argumentType == Integer.class ? 42 : argumentType.getDeclaredConstructor().newInstance();
        Field serviceField = controllerType.getDeclaredField((String) endpoint[3]);
        serviceField.setAccessible(true);
        Object service = mock(serviceField.getType());
        serviceField.set(controller, service);
        Method serviceMethod = serviceField.getType().getMethod((String) endpoint[4], argumentType);
        when(serviceMethod.invoke(service, argument)).thenReturn(message);

        JSONObject response = (JSONObject) controllerType.getMethod((String) endpoint[1], argumentType)
                .invoke(controller, argument);

        assertEquals(1, response.size());
        assertTrue(response.containsKey("message"));
        assertEquals(message, response.get("message"));
        serviceMethod.invoke(verify(service), argument);
        verifyNoMoreInteractions(service);
    }
}
