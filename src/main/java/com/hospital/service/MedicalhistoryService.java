package com.hospital.service;

import com.hospital.entity.Medicalhistory;

import java.util.List;

public interface MedicalhistoryService {
    List<Medicalhistory> getAllMedicalhistorys();
    String delMedicalhistory(Integer id);
    Medicalhistory getMedicalhistory(Integer id);
    String UpdateMedicalhistory(Medicalhistory medicalhistory);
    String addMedicalhistory(Medicalhistory medicalhistory);
}
