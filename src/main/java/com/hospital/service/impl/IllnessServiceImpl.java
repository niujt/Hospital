package com.hospital.service.impl;

import com.hospital.common.CommonService;
import com.hospital.dao.IllnessMapper;
import com.hospital.entity.Illness;
import com.hospital.service.IllnessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class IllnessServiceImpl implements IllnessService {
    @Autowired
    IllnessMapper illnessMapper;
    @Override
    public List<Illness> getAll() {
        return illnessMapper.selectAll();
    }

    @Override
    public Illness getIllness(Integer id) {
        return illnessMapper.getIllness(id);
    }

    @Override
    public String insert(Illness option) {
        return illnessMapper.insert(option)>0? CommonService.add_message_success:CommonService.add_message_error;
    }

    @Override
    public String deleteIllness(Integer id) {
        return illnessMapper.deleteById(id)>0?CommonService.del_message_success:CommonService.del_message_error;
    }

    @Override
    public String updateIllness(Illness option) {
        return illnessMapper.updateById(option)>0?CommonService.upd_message_success:CommonService.upd_message_error;
    }
}
