package com.hospital.service;

import com.hospital.entity.Patient;

import java.util.List;

public interface PatientService {
    List<Patient> getAllPatients(String name,String certId);
    List<Patient> getAllPatients();
    String delPatient(Integer id);
    Patient getPatient(Integer id);
    String updatePatient(Patient patient);
    String addPatient(Patient patient);
    String seek(Patient patient);
    Patient findPatientByLoginId(Integer loginid);
    String updateAppointMent(Patient patient);
}
