package com.hospital.dao;

import com.hospital.entity.Doctor;

import java.util.List;

public interface DoctorMapper {
    List<Doctor> getAll();
    int deleteByPrimaryKey(Integer id);

    int insert(Doctor record);

    int insertSelective(Doctor record);

    Doctor selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Doctor record);

    int updateByPrimaryKey(Doctor record);
}