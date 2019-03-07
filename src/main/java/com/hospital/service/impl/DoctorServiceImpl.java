package com.hospital.service.impl;

import com.hospital.dao.DoctorMapper;
import com.hospital.entity.Doctor;
import com.hospital.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class DoctorServiceImpl implements DoctorService {
    @Autowired
    DoctorMapper doctorMapper;
    @Override
    public List<Doctor> getAllDoctor() {
        return doctorMapper.getAll();
    }
}
