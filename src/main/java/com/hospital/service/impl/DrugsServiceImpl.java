package com.hospital.service.impl;

import com.hospital.dao.DrugsMapper;
import com.hospital.entity.Drugs;
import com.hospital.service.DrugsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DrugsServiceImpl implements DrugsService {
    @Autowired
    DrugsMapper drugsMapper;

    @Override
    public List<Drugs> getAllDrugs() {
        return drugsMapper.findAll();
    }
}
