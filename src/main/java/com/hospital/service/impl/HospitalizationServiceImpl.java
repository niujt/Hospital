package com.hospital.service.impl;

import com.hospital.dao.HospitalizationMapper;
import com.hospital.entity.Hospitalization;
import com.hospital.service.HospitalizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HospitalizationServiceImpl implements HospitalizationService {
    @Autowired
    HospitalizationMapper hospitalizationMapper;

    @Override
    public List<Hospitalization> getAllHospitalizations() {
        return hospitalizationMapper.findAll();
    }
}
