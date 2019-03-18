package com.hospital.service;

import com.hospital.entity.Hospitalization;

import java.util.List;

public interface HospitalizationService {
    List<Hospitalization> getAllHospitalizations();
    String AddHospitalization(Hospitalization hospitalization);
    String deleteHospitalization(Integer id);
}
