package com.hospital.dao;

import com.hospital.entity.Medicalhistory;

public interface MedicalhistoryMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Medicalhistory record);

    int insertSelective(Medicalhistory record);

    Medicalhistory selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Medicalhistory record);

    int updateByPrimaryKey(Medicalhistory record);
}