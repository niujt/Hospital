package com.hospital.service;

import com.hospital.entity.Hospitalization;

import java.util.Date;
import java.util.List;

public interface HospitalizationService {
    List<Hospitalization> getAllHospitalizations(String patientname, String intime);
    List<Hospitalization> getAllHospitalizations();
    String AddHospitalization(Hospitalization hospitalization);
    String deleteHospitalization(Integer id);
    Hospitalization getHospitalization(Integer id);
    String updateHospitalization(Hospitalization hospitalization);
    List<Hospitalization> getPatientMessage(Integer patientId);
    Hospitalization findTheLastHospitalization(Integer id);
    List<Hospitalization> findOtherHospitalization(Hospitalization hospitalization);
}
