package com.hospital.service.impl;

import com.hospital.dao.MedicalhistoryMapper;
import com.hospital.entity.Medicalhistory;
import com.hospital.service.MedicalhistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class MedicalhistoryServiceImpl implements MedicalhistoryService {
    @Autowired
    MedicalhistoryMapper medicalhistoryMapper;
    @Override
    public List<Medicalhistory> getAllMedicalhistorys() {
        return medicalhistoryMapper.findAll();
    }
}
