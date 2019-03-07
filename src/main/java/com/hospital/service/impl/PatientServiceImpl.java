package com.hospital.service.impl;

import com.hospital.dao.PatientMapper;
import com.hospital.entity.Patient;
import com.hospital.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class PatientServiceImpl implements PatientService {
    @Autowired
    PatientMapper patientMapper;
    @Override
    public List<Patient> getAllPatients() {
        return patientMapper.findAll();
    }
}
