package com.hospital.service.impl;

import com.hospital.common.CommonService;
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

    @Override
    public String AddHospitalization(Hospitalization hospitalization) {
        return hospitalizationMapper.insert(hospitalization)>0? CommonService.add_message_success:CommonService.add_message_error;
    }

    @Override
    public String deleteHospitalization(Integer id) {
        return hospitalizationMapper.deleteByPrimaryKey(id)>0?CommonService.del_message_success:CommonService.add_message_error;
    }
}
