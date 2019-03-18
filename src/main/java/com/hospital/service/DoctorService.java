package com.hospital.service;

import com.hospital.entity.Doctor;

import java.util.List;

public interface DoctorService {
    List<Doctor> getAllDoctor();
    String delDoctor(Integer id);
}
