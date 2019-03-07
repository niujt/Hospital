package com.hospital.dao;

import com.hospital.entity.Hospitalization;

public interface HospitalizationMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Hospitalization record);

    int insertSelective(Hospitalization record);

    Hospitalization selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Hospitalization record);

    int updateByPrimaryKey(Hospitalization record);
}