package com.hospital.service.impl;

import com.hospital.common.CommonService;
import com.hospital.dao.DoctorMapper;
import com.hospital.dao.MedicalhistoryMapper;
import com.hospital.dao.PatientMapper;
import com.hospital.entity.Medicalhistory;
import com.hospital.service.MedicalhistoryService;
import com.hospital.uitls.PatientDoctorutils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class MedicalhistoryServiceImpl implements MedicalhistoryService {
    @Autowired
    MedicalhistoryMapper medicalhistoryMapper;
    @Autowired
    DoctorMapper doctorMapper;
    @Autowired
    PatientMapper patientMapper;
    @Override
    public List<Medicalhistory> getAllMedicalhistorys(String doctorname,String patientname) {
        Map map= PatientDoctorutils.getDoctorIdsAndPatientIds(doctorname,doctorMapper,patientname,patientMapper);
        return medicalhistoryMapper.findAll((List)map.get("doctorids"),(List)map.get("patientids"));
    }

    @Override
    public String delMedicalhistory(Integer id) {
        return medicalhistoryMapper.deleteByPrimaryKey(id)>0? CommonService.del_message_success:CommonService.del_message_error;
    }

    @Override
    public Medicalhistory getMedicalhistory(Integer id) {
        return medicalhistoryMapper.selectByPrimaryKey(id);
    }

    @Override
    public String UpdateMedicalhistory(Medicalhistory medicalhistory) {
        return medicalhistoryMapper.updateByPrimaryKey(medicalhistory)>0?CommonService.upd_message_success:CommonService.upd_message_error;
    }

    @Override
    public String addMedicalhistory(Medicalhistory medicalhistory) {
        return medicalhistoryMapper.insert(medicalhistory)>0?CommonService.add_message_success:CommonService.add_message_error;
    }

    @Override
    public List<Medicalhistory> getMedicalhistoryByPatientId(Integer id) {
        return medicalhistoryMapper.selectByPatientId(id);
    }
}
