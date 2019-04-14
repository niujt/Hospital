package com.hospital.service;

import com.hospital.entity.Medicalhistory;

import java.util.List;

public interface MedicalhistoryService {
    List<Medicalhistory> getAllMedicalhistorys(String doctorname,String patientname);
    String delMedicalhistory(Integer id);
    Medicalhistory getMedicalhistory(Integer id);
    String UpdateMedicalhistory(Medicalhistory medicalhistory);
    String addMedicalhistory(Medicalhistory medicalhistory);
    List<Medicalhistory> getMedicalhistoryByPatientId(Integer id);
}
