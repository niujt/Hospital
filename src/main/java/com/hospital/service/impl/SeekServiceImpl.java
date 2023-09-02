package com.hospital.service.impl;

import com.hospital.common.CommonService;
import com.hospital.dao.SeekMapper;
import com.hospital.entity.Seek;
import com.hospital.service.SeekService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SeekServiceImpl implements SeekService {
    @Autowired
    SeekMapper seekMapper;
    @Override
    public String addSeek(Seek seek) {
        return seekMapper.insert(seek)>0? CommonService.add_message_success:CommonService.add_message_error;
    }

    @Override
    public Seek getSeekByPatientId(Integer patientid) {
        return seekMapper.getSeekByPatientId(patientid);
    }
}
