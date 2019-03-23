package com.hospital.service.impl;

import com.hospital.common.CommonService;
import com.hospital.dao.LoginMapper;
import com.hospital.dao.PatientMapper;
import com.hospital.entity.Login;
import com.hospital.entity.Patient;
import com.hospital.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class PatientServiceImpl implements PatientService {
    @Autowired
    PatientMapper patientMapper;
    @Autowired
    LoginMapper loginMapper;
    @Override
    public List<Patient> getAllPatients() {
        return patientMapper.findAll();
    }

    @Override
    public String delPatient(Integer id) {
        return patientMapper.deleteByPrimaryKey(id)>0? CommonService.del_message_success:CommonService.del_message_error;
    }

    @Override
    public Patient getPatient(Integer id) {
        Patient patient=patientMapper.selectByPrimaryKey(id);
        Login login=loginMapper.selectByPrimaryKey(patient.getLoginid());
        patient.setUsername(login.getUsername());
        patient.setPassword(login.getPassword());
        return patient;
    }
}
