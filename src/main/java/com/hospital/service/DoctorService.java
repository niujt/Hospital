package com.hospital.service;

import com.hospital.entity.Doctor;

import javax.print.Doc;
import java.util.List;

public interface DoctorService {
    List<Doctor> getAllDoctor();
    List<Doctor> getAllDoctor(String name,String certId);
    String delDoctor(Integer id);
    String addDoctor(Doctor doctor);
    Doctor getDoctor(Integer id);
    String upDoctor(Doctor doctor);
    Doctor getDoctorByLoginId(Integer loginid);
}
