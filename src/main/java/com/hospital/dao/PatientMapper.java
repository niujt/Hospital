package com.hospital.dao;

import com.hospital.entity.Patient;

import java.util.List;

public interface PatientMapper {
    List<Patient> findAll();
    int deleteByPrimaryKey(Integer id);

    int insert(Patient record);

    int insertSelective(Patient record);

    Patient selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Patient record);

    int updateByPrimaryKey(Patient record);
}