package com.hospital.dao;

import com.hospital.entity.Medicalhistory;

import java.util.List;

public interface MedicalhistoryMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Medicalhistory record);

    int insertSelective(Medicalhistory record);

    Medicalhistory selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Medicalhistory record);

    int updateByPrimaryKey(Medicalhistory record);
    List<Medicalhistory> findAll();
}