package com.hospital.service.impl;

import com.hospital.common.CommonService;
import com.hospital.dao.DoctorMapper;
import com.hospital.dao.LoginMapper;
import com.hospital.dao.PatientMapper;
import com.hospital.entity.Doctor;
import com.hospital.entity.Login;
import com.hospital.service.DoctorService;
import com.hospital.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class DoctorServiceImpl implements DoctorService {
    @Autowired
    DoctorMapper doctorMapper;
    @Autowired
    PatientMapper patientMapper;
    @Autowired
    LoginMapper loginMapper;
    @Override
    public List<Doctor> getAllDoctor() {
        return doctorMapper.getAll("","");
    }

    @Override
    public List<Doctor> getAllDoctor(String name, String certId) {
        return doctorMapper.getAll(name,certId);
    }

    @Override
    public String delDoctor(Integer id) {
        return doctorMapper.deleteByPrimaryKey(id)>0? CommonService.del_message_success:CommonService.del_message_error;
    }

    @Override
    public String addDoctor(Doctor doctor) {
        String message="";
        if(doctorMapper.getDoctorByCertId(doctor.getCertId())!=null||patientMapper.findPatientByCertId(doctor.getCertId())!=null){
            message="该身份证已被注册或使用";
        }
        else {
            message=doctorMapper.insert(doctor)>0?CommonService.add_message_success:CommonService.add_message_error;
        }
        return message;
    }

    @Override
    public Doctor getDoctor(Integer id) {
        Doctor doctor=doctorMapper.selectByPrimaryKey(id);
        Login login=loginMapper.selectByPrimaryKey(doctor.getLoginid());
        doctor.setUsername(login.getUsername());
        doctor.setPassword(login.getPassword());
        return doctor;
    }

    @Override
    public String upDoctor(Doctor doctor) {
        Login login=loginMapper.selectByPrimaryKey(doctor.getLoginid());
        login.setUsername(doctor.getUsername());
        login.setPassword(doctor.getPassword());
        login.setId(doctor.getLoginid());
        return (doctorMapper.updateByPrimaryKey(doctor)>0&&loginMapper.updateByPrimaryKey(login)>0)?CommonService.upd_message_success:CommonService.upd_message_error;
    }

    @Override
    public Doctor getDoctorByLoginId(Integer loginid) {
        return doctorMapper.getDoctorByLoginId(loginid);
    }
}
